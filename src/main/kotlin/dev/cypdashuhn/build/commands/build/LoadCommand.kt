package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.worldedit.worldEditRegionArgument
import dev.cypdashuhn.rooster.common.region.Region
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.entity.Player

val load = CommandAPICommand("!load")
    .withArguments(buildNameArgument())
    .withArguments(worldEditRegionArgument())
    .withOptionalArguments(CustomArgument(IntegerArgument("frame")) { info ->
        val build: DbBuildsManager.Build by info.previousArgs.argsMap
        val acceptibleFrames = 1..build.frameAmount
        if (!acceptibleFrames.contains(info.currentInput)) throw error("Frame ${info.currentInput} is not between 1 and ${build.frameAmount}")
        info.currentInput
    })
    .executes(CommandExecutor { sender, args ->
        val build: DbBuildsManager.Build by args.argsMap
        val selection: Region by args.argsMap
        val frame: Int? by args.argsMap

        if (frame != null) BuildManager.load(sender as Player, build, frame!!, selection)
        else BuildManager.loadAll(sender as Player, build, selection)
    })
    .register()