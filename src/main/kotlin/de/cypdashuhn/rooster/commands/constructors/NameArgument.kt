package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.ArgumentInfo
import de.cypdashuhn.rooster.commands.IsValidResult
import de.cypdashuhn.rooster.commands.UnfinishedArgument
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
            suggestions = { listOf(t("rooster.name.placeholder")) },
            isValid = isValid
        )
    }

    fun unique(
        usedNames: List<String>,
        key: String = "name",
        uniqueErrorKey: String = "rooster.name.used",
        isValid: ((ArgumentInfo) -> IsValidResult)? = null
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            suggestions = { listOf(t("rooster.name.placeholder")) },
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
        uniqueErrorKey: String = "rooster.name.used",
        nameArg: String = "name"
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            suggestions = { listOf(t("rooster.name.placeholder")) },
            isValid = {
                transaction {
                    var query = targetColumn eq it.arg
                    extraQuery?.let { query = query and it }

                    if (table.selectAll().where { query }.firstOrNull() != null) {
                        IsValidResult.Invalid { info -> info.sender.tSend(uniqueErrorKey, nameArg to it.arg) }
                    } else {
                        isValid?.invoke(it) ?: IsValidResult.Valid()
                    }
                }
            }
        )
    }
}