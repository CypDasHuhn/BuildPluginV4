package dev.cypdashuhn.build.commands

import dev.cypdashuhn.build.commands.wrapper.simpleSuggestions
import dev.jorel.commandapi.AbstractArgumentTree
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.command.CommandSender

fun test3() = CommandTree("!test3")
    .then(LiteralArgument("branch1").executes(CommandExecutor { sender, info -> sender.sendMessage("test1") }))
    .then(LiteralArgument("branch2").executes(CommandExecutor { sender, info -> sender.sendMessage("test2") }))
    .register()

fun test2() {
    CommandTree("!test2").useMultiple(
        IntegerArgument("number"),
        TextArgument("text").simpleSuggestions("last", "new", "after-last"),
        block = {
            executes(CommandExecutor { sender, info ->
                val number = info.argsMap["number"] as Int?
                val alt = info.argsMap["text"] as String?

                if (number != null) sender.sendMessage("Number: $number")
                if (alt != null) sender.sendMessage("Alt: $alt")
            })
        },
        last = { register() }
    )
}

fun test() = CommandTree("!test").useMultiple(
    LiteralArgument("branch1"),
    LiteralArgument("branch2"),
    block = {
        executes(CommandExecutor { sender, info ->
            val branch = info.argsMap["branch1"] as String?
            val branch2 = info.argsMap["branch2"] as String?

            if (branch != null) sender.sendMessage("Branch 1 selected")
            if (branch2 != null) sender.sendMessage("Branch 2 selected")
            sender.sendMessage("either or")
        })
    },
    last = { register() }
)

fun CommandTree.useMultiple(
    vararg args: Argument<*>,
    block: Argument<*>.() -> AbstractArgumentTree<*, Argument<*>, CommandSender>,
    last: CommandTree.() -> Unit
) {
    var tree = this
    val suggestionList = mutableListOf<ArgumentSuggestions<CommandSender>>()

    val lastIdx = args.size - 1
    args.forEachIndexed { idx, arg ->
        val isLast = idx == lastIdx

        if (!isLast) {
            if (arg.includedSuggestions.isPresent) suggestionList += arg.includedSuggestions.get()
            arg.replaceSuggestions(ArgumentSuggestions.empty())
            tree = tree.then(arg.block())
        }
        if (isLast) {
            if (arg.includedSuggestions.isPresent) suggestionList += arg.suggestions
            arg.replaceSuggestions(ArgumentSuggestions.merge(*suggestionList.toTypedArray()))
        }
    }
    tree.last()
}