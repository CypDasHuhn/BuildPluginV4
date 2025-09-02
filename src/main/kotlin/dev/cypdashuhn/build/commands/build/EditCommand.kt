package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.worldedit.worldEditRegionArgument
import dev.cypdashuhn.rooster.common.region.Region
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

fun edit() = CommandAPICommand("!edit")
    .withArguments(buildNameArgument())
    .withArguments(worldEditRegionArgument())
    .withArguments(frameArgument(1))
    .executes(CommandExecutor { sender, args ->
        val build: DbBuildsManager.Build by args.argsMap
        val selection: Region by args.argsMap
        val frame: Int by args.argsMap

        val success = BuildManager.save(sender as Player, build, frame, selection)
        if (success) sender.sendMessage(Component.translatable("build.edit.success"))
    })
    .register()