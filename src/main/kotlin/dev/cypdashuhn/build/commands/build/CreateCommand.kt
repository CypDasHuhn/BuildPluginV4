package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.commands.wrapper.existsByColumn
import dev.cypdashuhn.build.commands.wrapper.nameArgument
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.worldedit.worldEditRegionArgument
import dev.cypdashuhn.rooster.common.region.Region
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

fun create() = CommandTree("!create")
    .then(
        nameArgument("name", existsByColumn(DbBuildsManager.Builds.name))
            .then(
                worldEditRegionArgument()
                    .executesPlayer(PlayerCommandExecutor { sender, args ->
                        val name: String by args.argsMap
                        val selection: Region by args.argsMap

                        BuildManager.create(sender as Player, name, selection)
                    })
            )
    ).register()