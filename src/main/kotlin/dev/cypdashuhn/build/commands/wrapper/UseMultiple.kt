package dev.cypdashuhn.build.commands.wrapper

import dev.jorel.commandapi.AbstractArgumentTree
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.*
import org.bukkit.command.CommandSender
import java.util.*

fun AbstractArgumentTree<*, Argument<*>, CommandSender>.thenMerged(
    vararg args: Argument<*>,
    block: Argument<*>.() -> AbstractArgumentTree<*, Argument<*>, CommandSender>,
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

    return tree
}

fun AbstractArgumentTree<*, Argument<*>, CommandSender>.thenMerged(
    argList: List<Argument<*>>,
    block: Argument<*>.() -> AbstractArgumentTree<*, Argument<*>, CommandSender>,
) = this.thenMerged(args = argList.toTypedArray(), block)

fun AbstractArgumentTree<*, Argument<*>, CommandSender>.thenNested(vararg args: MergedArgument): AbstractArgumentTree<*, Argument<*>, CommandSender> {
    var tree = this

    var argIndex = 0
    while (true) {
        if (argIndex >= args.size) break
        val arg = args[argIndex]

        if (arg.isSingle) tree = tree.then(arg.arguments.first())
        else return tree.thenMerged(arg.arguments) {
            thenNested(*args.drop(argIndex + 1).toTypedArray())
        }
        argIndex++
    }

    return tree
}

fun test() {
    CommandTree("!test").then(
        LiteralArgument("test").thenNested(
            TextArgument("test1").single(),
            MergedArgument.of(IntegerArgument("number"), TextArgument("text")),
            TextArgument("test2").single(),
        )
    )
}

class MergedArgument(val arguments: List<Argument<*>>) {
    val isSingle get() = arguments.size == 1

    companion object {
        fun of(vararg args: Argument<*>) = MergedArgument(args.toList())
    }
}

fun Argument<*>.single() = MergedArgument(listOf(this))