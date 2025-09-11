package dev.cypdashuhn.build.commands

import dev.cypdashuhn.build.commands.wrapper.simpleSuggestions
import dev.cypdashuhn.build.commands.wrapper.thenMerged
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandExecutor

fun test4() = CommandTree("!test4").thenNested(
    TextArgument("pre-arg"),
    TextArgument("pre-arg-2").thenMerged(
        IntegerArgument("branch1").simpleSuggestions("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
        TextArgument("branch2").simpleSuggestions("-last", "-after-last", "-new"),
        block = {
            executes(CommandExecutor { sender, info ->
                val preArg = info.argsMap["pre-arg"] as String?
                sender.sendMessage("Pre-arg: $preArg")

                val preArg2 = info.argsMap["pre-arg-2"] as String?
                sender.sendMessage("Pre-arg-2: $preArg2")

                val branch1 = info.argsMap["branch1"] as Int?
                val branch2 = info.argsMap["branch2"] as String?

                if (branch1 != null) sender.sendMessage("Branch 1: $branch1")
                if (branch2 != null) sender.sendMessage("Branch 2: $branch2")
            })
        }
    )
).register()

fun test3() = CommandTree("!test3")
    .then(LiteralArgument("branch1").executes(CommandExecutor { sender, info -> sender.sendMessage("test1") }))
    .then(LiteralArgument("branch2").executes(CommandExecutor { sender, info -> sender.sendMessage("test2") }))
    .register()

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


