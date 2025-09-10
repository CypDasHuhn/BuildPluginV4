package dev.cypdashuhn.build.commands.wrapper

import dev.jorel.commandapi.AbstractArgumentTree
import dev.jorel.commandapi.AbstractCommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import java.lang.reflect.Field
import java.util.*

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


fun AbstractCommandTree<*, Argument<*>, CommandSender>.useMultiple(
    vararg args: Argument<*>,
    block: Argument<*>.() -> AbstractArgumentTree<*, Argument<*>, CommandSender>,
    last: AbstractCommandTree<*, Argument<*>, CommandSender>.() -> Unit = { }
): AbstractCommandTree<*, Argument<*>, CommandSender> {
    var tree = this

    val suggestionList: MutableList<ArgumentSuggestions<CommandSender>> = mutableListOf()

    args.forEachIndexed { idx, arg ->
        val isLast = idx == args.size - 1
        if (arg.overriddenSuggestions.isPresent) suggestionList += arg.overriddenSuggestions.get()
        if (!isLast) {
            findFieldRecursive(arg::class.java, "suggestions").apply {
                isAccessible = true
                set(arg, Optional.empty<ArgumentSuggestions<CommandSender>>())
            }
        }
        if (isLast) tree =
            tree.then(arg.replaceSuggestions(ArgumentSuggestions.merge(*suggestionList.toTypedArray())).block())
        tree = tree.then(arg.block())
    }
    tree.last()

    return tree
}

fun t() {
    CustomArgument<String, String>(TextArgument("test")) {
        ""
    }
}

fun AbstractArgumentTree<*, Argument<*>, CommandSender>.useMultiple(
    vararg args: Argument<*>,
    block: Argument<*>.() -> AbstractArgumentTree<*, Argument<*>, CommandSender>,
    last: AbstractArgumentTree<*, Argument<*>, CommandSender>.() -> Unit = { }
): AbstractArgumentTree<*, Argument<*>, CommandSender> {
    var tree = this

    val suggestionList: MutableList<ArgumentSuggestions<CommandSender>> = mutableListOf()

    args.forEachIndexed { idx, arg ->
        val isLast = idx == args.size - 1
        if (arg.overriddenSuggestions.isPresent) suggestionList += arg.overriddenSuggestions.get()
        if (!isLast) {
            findFieldRecursive(arg::class.java, "suggestions").apply {
                isAccessible = true
                set(arg, Optional.empty<ArgumentSuggestions<CommandSender>>())
            }
        }
        if (isLast) tree =
            tree.then(arg.replaceSuggestions(ArgumentSuggestions.merge(*suggestionList.toTypedArray())).block())
        tree = tree.then(arg.block())
    }
    tree.last()

    return tree
}

fun findFieldRecursive(clazz: Class<*>, name: String): Field {
    var current: Class<*>? = clazz
    while (current != null) {
        try {
            return current.getDeclaredField(name)
        } catch (_: NoSuchFieldException) {
            current = current.superclass
        }
    }
    throw NoSuchFieldException(name)
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

fun <T, B> Argument<T>.transform(parser: CustomArgument.CustomArgumentInfoParser<B, T>): CustomArgument<B, T> {
    return CustomArgument<B, T>(this, parser)
}
