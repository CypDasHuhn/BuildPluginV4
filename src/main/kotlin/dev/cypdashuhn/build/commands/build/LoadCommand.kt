package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.worldedit.worldEditRegionArgument
import dev.cypdashuhn.rooster.common.region.Region
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

fun load() = CommandAPICommand("!load")
    .withArguments(buildNameArgument())
    .withArguments(worldEditRegionArgument())
    .withOptionalArguments(frameArgument(0))
    .executes(CommandExecutor { sender, args ->
        val build: DbBuildsManager.Build by args.argsMap
        transaction { build.refresh() }
        val selection: Region by args.argsMap
        val frame: Int? = args.argsMap["frame"] as Int?

        if (frame != null) BuildManager.load(sender as Player, build, frame, selection)
        else BuildManager.loadAll(sender as Player, build, selection)
    })
    .register()