package dev.cypdashuhn.build.commands.wrapper

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll

fun error(value: String, appendInput: Boolean = false): CustomArgument.CustomArgumentException {
    return CustomArgument.CustomArgumentException.fromMessageBuilder(
        CustomArgument.MessageBuilder(value).run {
            if (appendInput) appendArgInput() else this
        }
    )
}

fun <T> Argument<T>.simpleSuggestions(vararg suggestion: String): Argument<T> {
    return replaceSuggestions(ArgumentSuggestions.strings { suggestion })
}

fun existsByList(list: List<String>): ((String) -> Boolean) = { list.contains(it) }
fun existsByColumn(column: Column<String>): ((String) -> Boolean) = { column.valueByColumn(it) != null }

fun <T> Column<T>.valueByColumn(value: T): ResultRow? {
    val table = this.table
    return table.selectAll().where(this eq value).firstOrNull()
}

fun CommandTree.useMultiple(
    vararg args: Argument<*>,
    block: CommandTree.() -> Unit
) {
    args.forEach { arg ->
        this.then(arg).block()
    }
}