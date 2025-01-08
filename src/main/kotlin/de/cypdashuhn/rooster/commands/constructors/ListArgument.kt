package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.core.Rooster.cache
import de.cypdashuhn.rooster.localization.tSend
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object ListArgument {
    fun single(
        key: String,
        list: List<String>,
        ignoreCase: Boolean = false,
        prefix: String = "",
        notMatchingError: (ArgumentInfo, String) -> Unit,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        onMissing: (ArgumentInfo) -> Unit,
        isValid: ((ArgumentInfo, String) -> IsValidResult)? = null,
        transformValue: ((ArgumentInfo, String) -> Any)? = null,
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            onMissing = onMissing,
            isValid = {
                val arg = it.arg.substring(prefix.length)

                if (list.none { it.equals(arg, ignoreCase) }) {
                    notMatchingError(it, arg)
                }

                if (isValid != null) {
                    return@UnfinishedArgument isValid(it, arg)
                }

                IsValidResult.Valid()
            },
            transformValue = {
                val arg = it.arg.substring(prefix.length)

                transformValue?.invoke(it, arg) ?: arg
            },
            suggestions = { list.map { "$prefix$it" } }
        )
    }

    data class DBCacheInfo(
        val query: Query,
        val arg: String
    )

    private const val LIST_FILTERED_CACHE_KEY = "rooster_list_cache_filtered"
    private const val LIST_CACHE_KEY = "rooster_list_cache_filtered"

    fun <E : IntEntity> dbList(
        entity: IntEntityClass<E>,
        displayField: Column<String>,
        filter: ((ArgumentInfo, E) -> Boolean)? = null,
        ignoreCase: Boolean = false,
        key: String,
        errorInvalidMessageKey: String,
        argKey: String = "arg",
        errorMissingMessageKey: String,
        isArgument: ArgumentPredicate = { true },
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any) = { it.arg },
    ): UnfinishedArgument {
        return UnfinishedArgument(
            isTarget = isArgument,
            isEnabled = isValidCompleter,
            transformValue = transformValue,
            onArgumentOverflow = errorArgumentOverflow,
            isValid = { (sender, args, arg, index, values) ->
                transaction {
                    val condition = if (ignoreCase) displayField.lowerCase() eq arg.lowercase() else displayField eq arg

                    /*var cacheInfo = cache.getIfPresent(LIST_FILTERED_CACHE_KEY, sender)
                    cacheInfo =  cacheInfo as DBCacheInfo?*/
                    val query = entity.table.selectAll()

                    val entries = query.where { condition }

                    // Cache the entries
                    /*cache.put(LIST_FILTERED_CACHE_KEY, sender, DBCacheInfo(entries, arg), 5 * 1000)*/

                    val matchingEntries = entity.wrapRows(entries)

                    val filteredEntries = if (filter != null) matchingEntries.filter {
                        filter(ArgumentInfo(sender, args, arg, index, values), it)
                    } else matchingEntries

                    when {
                        filteredEntries.firstOrNull() == null -> IsValidResult.Invalid {
                            it.sender.tSend(
                                errorInvalidMessageKey,
                                argKey to it.arg
                            )
                        }

                        else -> IsValidResult.Valid()
                    }
                }
            },
            suggestions = { argInfo ->
                transaction {
                    val entries = cache.get(
                        LIST_CACHE_KEY,
                        argInfo.sender,
                        { entity.wrapRows(entity.table.selectAll()) },
                        5 * 1000
                    )

                    val matchingEntries = if (filter != null) entries.filter { filter(argInfo, it) } else entries
                    matchingEntries.map { it.readValues[displayField] }
                }
            },
            key = key,
            onMissing = errorMessage(errorMissingMessageKey),
        )
    }

    fun chainable(
        key: String,
        list: List<String>,
        prefix: String = "",
        splitter: String = ",",
        allowDuplications: Boolean = false,
        ignoreCase: Boolean = true,
        duplicationError: (ArgumentInfo, String) -> Unit = { _, _ ->
            throw IllegalArgumentException(
                "Missing Duplication Error"
            )
        },
        notMatchingError: (ArgumentInfo, String) -> Unit,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        onMissing: (ArgumentInfo) -> Unit,
        isValid: ((ArgumentInfo, String) -> IsValidResult)? = null,
        transformValue: ((ArgumentInfo, String) -> Any)? = null
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            onMissing = onMissing,
            suggestions = { info: ArgumentInfo ->
                val arg = info.arg
                val base = arg.substringBeforeLast(splitter)
                val lastAfterSplit = arg.substringAfterLast(splitter)

                if (base.isEmpty()) {
                    list.filter { it.startsWith(arg, ignoreCase) }.map { "$prefix$it" }
                } else {
                    val currentItems = base.split(splitter)

                    val filtered = (if (allowDuplications) list else list.filter { item ->
                        currentItems.none { it.equals(item, ignoreCase) }
                    }).filter { it.startsWith(lastAfterSplit, ignoreCase) }

                    filtered.map { "$base$splitter$it" }
                }
            },
            transformValue = { info: ArgumentInfo ->
                info.arg.split(splitter).map { if (transformValue != null) transformValue(info, it) else it }
            },
            isValid = { info: ArgumentInfo ->
                val values = info.arg.split(splitter)

                values.groupBy { it }.forEach { group ->
                    if (!allowDuplications) {
                        if (group.value.size > 1) return@UnfinishedArgument IsValidResult.Invalid {
                            duplicationError(info, group.key)
                        }
                    }

                    if (list.none { it.equals(group.key, ignoreCase) }) {
                        return@UnfinishedArgument IsValidResult.Invalid {
                            notMatchingError(info, group.key)
                        }
                    }

                    if (isValid != null) {
                        return@UnfinishedArgument isValid(info, group.key)
                    }
                }

                IsValidResult.Valid()
            }
        )
    }
}