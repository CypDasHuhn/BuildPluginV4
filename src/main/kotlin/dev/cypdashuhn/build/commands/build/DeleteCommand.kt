package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.Component

val delete = CommandAPICommand("!delete")
    .withArguments(buildNameArgument())
    .executes(CommandExecutor { sender, args ->
        val build: DbBuildsManager.Build by args.argsMap
        BuildManager.delete(build)
        sender.sendMessage(Component.translatable("build.delete.success"))
    })
    .register()