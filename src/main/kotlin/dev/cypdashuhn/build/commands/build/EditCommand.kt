package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.commands.wrapper.eitherOf
import dev.cypdashuhn.build.commands.wrapper.useMultiple
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.worldedit.worldEditRegionArgument
import dev.cypdashuhn.rooster.common.region.Region
import dev.jorel.commandapi.AbstractCommandTree
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun edit() = CommandTree("!edit").then(
    buildNameArgument().then(worldEditRegionArgument()).useMultiple(
        frameArgumentWithoutSuggestions(1),
        dynamicFrameArgument(),
        suggestions = ArgumentSuggestions.merge(frameArgumentSuggestions(1), dynamicFrameArgumentSuggestions()),
        block = {
            executes(CommandExecutor { sender, args ->
                val build: DbBuildsManager.Build by args.argsMap
                val selection: Region by args.argsMap
                val frame = args.eitherOf<Int>(frameKey, dynamicFrameKey)

                BuildManager.save(sender as Player, build, frame, selection)
            })
        },
    ) as AbstractCommandTree<*, Argument<*>, CommandSender>
)

fun edit_() = CommandTree("!edit").then(
    buildNameArgument().then(
        worldEditRegionArgument().then(
            LiteralArgument("static").then(
                frameArgument(1).executes(CommandExecutor { sender, args ->
                    val build: DbBuildsManager.Build by args.argsMap
                    val selection: Region by args.argsMap
                    val frame = args.getFrame()

                    BuildManager.save(sender as Player, build, frame, selection)
                })
            )
        ).then(
            LiteralArgument("dynamic").then(
                dynamicFrameArgument().executes(CommandExecutor { sender, args ->
                    val build: DbBuildsManager.Build by args.argsMap
                    val selection: Region by args.argsMap
                    val frame = args.getFrame()

                    BuildManager.save(sender as Player, build, frame, selection)
                })
            )
        )
    )
).register()
