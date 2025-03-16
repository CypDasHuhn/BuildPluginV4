package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.core.Rooster.cache
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.command.CommandSender
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
        key: String = "list",
        listFunc: (ArgumentInfo) -> List<String>,
        ignoreCase: Boolean = false,
        prefix: String = "",
        notMatchingError: (ArgumentInfo, String) -> Unit = { info, _ ->
            playerMessage("rooster.list.not_matching_error", "entry")(
                info
            )
        },
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        onMissing: (ArgumentInfo) -> Unit = { info -> playerMessage("rooster.list.missing")(info) },
        isValid: ((ArgumentInfo, String) -> IsValidResult)? = null,
        transformValue: ((ArgumentInfo, String) -> Any)? = null
    ): ListArgumentType {
        val arg = UnfinishedArgument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            onMissing = onMissing,
            isValid = {
                val arg = it.arg.substring(prefix.length)

                val list = listFunc(it)

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
            suggestions = { listFunc(it).map { "$prefix$it" } }
        )

        return ListArgumentType(arg, key)
    }

    fun single(
        key: String = "list",
        list: List<String>,
        ignoreCase: Boolean = false,
        prefix: String = "",
        notMatchingError: (ArgumentInfo, String) -> Unit = { info, _ ->
            playerMessage("rooster.list.not_matching_error")(
                info
            )
        },
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        onMissing: (ArgumentInfo) -> Unit = { info -> playerMessage("rooster.list.missing")(info) },
        isValid: ((ArgumentInfo, String) -> IsValidResult)? = null,
        transformValue: ((ArgumentInfo, String) -> Any)? = null
    ) = single(
        key,
        { list },
        ignoreCase,
        prefix,
        notMatchingError,
        isEnabled,
        isTarget,
        onMissing,
        isValid,
        transformValue
    )

    class ListArgumentType(arg: UnfinishedArgument, argKey: String) : SimpleArgumentType<String>("List", arg, argKey)

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
        key: String = "list",
        errorInvalidMessageKey: String = "rooster.list.invalid_error",
        argKey: String = "arg",
        errorMissingMessageKey: String = "rooster.list.missing_error",
        isArgument: ArgumentPredicate = { true },
        isValidCompleter: ArgumentPredicate? = null,
        transformValue: ((E) -> E) = { it },
    ): DbArgumentType<E> {
        val arg = UnfinishedArgument(
            isTarget = isArgument,
            isEnabled = isValidCompleter,
            transformValue = { argInfo ->
                transaction {
                    val condition =
                        if (ignoreCase) displayField.lowerCase() eq argInfo.arg.lowercase() else displayField eq argInfo.arg

                    val query = entity.table.selectAll()

                    val entries = query.where { condition }

                    val matchingEntries = entity.wrapRows(entries)

                    val filteredEntries = if (filter != null) matchingEntries.filter {
                        filter(argInfo, it)
                    } else matchingEntries

                    val entry = filteredEntries.firstOrNull()
                    requireNotNull(entry) { "Entry should've been canceled before'" }

                    val arg = transformValue(entry)

                    arg
                }
            },
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
            onMissing = playerMessage(errorMissingMessageKey),
        )

        return DbArgumentType(arg, key)
    }

    class DbArgumentType<T>(
        argument: UnfinishedArgument,
        val argKey: String
    ) : TypedArgument<T>(argument) {
        override fun value(sender: CommandSender, context: CommandContext): TypeResult<T> {
            val data = context[argKey] as? T?
                ?: return TypeResult.Failure(IllegalStateException("Data is null for DbList Argument"))

            return TypeResult.Success(data)
        }
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

    class DbListArgumentType<T>(
        argument: UnfinishedArgument,
        val argKey: String
    ) : TypedArgument<List<T>>(argument) {
        override fun value(sender: CommandSender, context: CommandContext): TypeResult<List<T>> {
            val data = context[argKey] as? List<T>?
                ?: return TypeResult.Failure(IllegalStateException("Data is null for DbList Argument"))

            return TypeResult.Success(data)
        }
    }
}