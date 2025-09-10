package dev.cypdashuhn.build.commands

import dev.cypdashuhn.build.commands.wrapper.*
import dev.cypdashuhn.build.commands.wrapper.collection.enumSuggestions
import dev.cypdashuhn.build.commands.wrapper.collection.toEnum
import dev.jorel.commandapi.CommandTree
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
        IntegerArgument("number").simpleSuggestions(*(0..10).toList().padded().toTypedArray()),
        TextArgument("text").simpleSuggestions("last", "after-last", "new"),
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
    TextArgument("branch1").transform(toEnum<TestEnum>()).replaceSuggestions(enumSuggestions<TestEnum>()),
    TextArgument("branch2").transform(toEnum<OtherEnum>()).replaceSuggestions(enumSuggestions<OtherEnum>()),
    block = {
        executes(CommandExecutor { sender, info ->
            {
                val s = info.eitherOf<Any>("branch1", "branch2")
                sender.sendMessage(s.toString())
            }
        })
    }
).register()

enum class TestEnum {
    TEST1,
    TEST2,
    TEST3
}

enum class OtherEnum {
    OTHER1,
    OTHER2,
    OTHER3
}


