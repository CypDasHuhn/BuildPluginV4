package dev.cypdashuhn.build.commands

import dev.cypdashuhn.build.commands.wrapper.padded
import dev.cypdashuhn.build.commands.wrapper.useMultiple
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandExecutor

fun test3() = CommandTree("!test3")
    .then(LiteralArgument("branch1").executes(CommandExecutor { sender, info -> sender.sendMessage("test1") }))
    .then(LiteralArgument("branch2").executes(CommandExecutor { sender, info -> sender.sendMessage("test2") }))
    .register()

fun test2() {
    CommandTree("!test2").useMultiple(
        IntegerArgument("number"),
        TextArgument("text"),
        suggestions = ArgumentSuggestions.strings {
            arrayOf(
                "-last",
                "-new",
                "-after-last"
            ) + (0..10).toList().padded()
        },
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
    suggestions = ArgumentSuggestions.strings { arrayOf("branch1", "branch2") },
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
