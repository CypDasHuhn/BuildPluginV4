package de.cypdashuhn.rooster.commands_new.utility_constructors

import de.cypdashuhn.rooster.commands_new.constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands_new.constructors.IsValidResult
import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object NameArgument {
    fun simple(
        key: String = "name",
        isValid: ((ArgumentInfo) -> IsValidResult)? = null
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            suggestions = { listOf(t("rooster.name_placeholder")) },
            isValid = isValid
        )
    }

    fun unique(
        usedNames: List<String>,
        key: String = "name",
        uniqueErrorKey: String = "rooster.name_used",
        isValid: ((ArgumentInfo) -> IsValidResult)? = null
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            suggestions = { listOf(t("rooster.name_placeholder")) },
            isValid = {
                if (usedNames.contains(it.arg)) {
                    IsValidResult.Invalid { info -> info.sender.tSend(uniqueErrorKey) }
                } else {
                    isValid?.invoke(it) ?: IsValidResult.Valid()
                }
            }
        )
    }

    fun unique(
        table: Table,
        targetColumn: Column<String>,
        extraQuery: Op<Boolean>? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        key: String = "name",
        uniqueErrorKey: String = "rooster.name_used"
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            suggestions = { listOf(t("rooster.name_placeholder")) },
            isValid = {
                transaction {
                    var query = targetColumn eq it.arg
                    extraQuery?.let { query = query and it }

                    if (table.selectAll().where { query }.firstOrNull() != null) {
                        IsValidResult.Invalid { info -> info.sender.tSend(uniqueErrorKey) }
                    } else {
                        isValid?.invoke(it) ?: IsValidResult.Valid()
                    }
                }
            }
        )
    }
}