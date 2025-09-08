package dev.cypdashuhn.build.commands.wrapper

import dev.jorel.commandapi.AbstractArgumentTree
import dev.jorel.commandapi.AbstractCommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.command.CommandSender
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

fun <T> AbstractArgumentTree<T, Argument<*>, CommandSender>.useMultiple(
    vararg args: Argument<*>,
    suggestions: ArgumentSuggestions<CommandSender>,
    block: Argument<*>.() -> AbstractArgumentTree<*, Argument<*>, CommandSender>,
    last: AbstractArgumentTree<*, Argument<*>, CommandSender>.() -> Unit = { }
): AbstractCommandTree<*, Argument<*>, CommandSender> {
    var tree = this

    args.forEachIndexed { idx, arg ->
        val isLast = idx == args.size - 1
        if (isLast) tree = tree.then(arg.replaceSuggestions(suggestions).block())
        tree = tree.then(arg.block())
    }
    tree.last()

    return tree
}

inline fun <reified T> CommandArguments.eitherOf(vararg nodeNames: String): T {
    argsMap.filter { it.key in nodeNames }.forEach {
        if (it.value is T) return it.value as T
    }
    throw error("No value found")
}

inline fun <reified T> CommandArguments.safeEitherOf(vararg nodeNames: String): T? {
    argsMap.filter { it.key in nodeNames }.forEach {
        if (it.value is T) return it.value as T
    }
    return null
}

fun List<Int>.padded(): List<String> {
    val max = this.maxOrNull() ?: 0
    return map { it.toString().padStart(max.toString().length, '0') }
}
