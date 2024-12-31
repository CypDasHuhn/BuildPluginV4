package de.cypdashuhn.rooster.commands_new.utility_constructors

import de.cypdashuhn.rooster.commands_new.constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands_new.constructors.ArgumentPredicate
import de.cypdashuhn.rooster.commands_new.constructors.IsValidResult
import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument

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