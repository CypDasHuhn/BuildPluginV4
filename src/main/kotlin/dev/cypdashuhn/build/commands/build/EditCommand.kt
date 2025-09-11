package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.commands.build.general.broaderFrame
import dev.cypdashuhn.build.commands.build.general.getFrame
import dev.cypdashuhn.build.commands.wrapper.thenMerged
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.worldedit.worldEditRegionArgument
import dev.cypdashuhn.rooster.common.region.Region
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.entity.Player

fun edit() = CommandTree("!edit").then(
    buildNameArgument().then(
        worldEditRegionArgument().thenMerged(
            broaderFrame(),
            block = {
                executes(CommandExecutor { sender, args ->
                    val build: DbBuildsManager.Build by args.argsMap
                    val selection: Region by args.argsMap
                    val frame = args.getFrame()

                    BuildManager.save(sender as Player, build, frame, selection)
                })
            },
        )
    )
).register()
