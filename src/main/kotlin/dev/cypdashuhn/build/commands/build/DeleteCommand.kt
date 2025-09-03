package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor

fun delete() = CommandAPICommand("!delete")
    .withArguments(buildNameArgument())
    .executes(CommandExecutor { sender, args ->
        val build: DbBuildsManager.Build by args.argsMap
        BuildManager.delete(sender as org.bukkit.entity.Player, build)
    })
    .register()