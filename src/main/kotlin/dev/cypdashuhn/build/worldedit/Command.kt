package dev.cypdashuhn.build.worldedit

import dev.cypdashuhn.build.commands.wrapper.error
import dev.cypdashuhn.rooster.common.region.Region
import dev.cypdashuhn.rooster.common.util.location
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.LocationArgument
import org.bukkit.Location
import org.bukkit.entity.Player

fun worldEditRegionArgument(name: String = "selection"): Argument<Region> {
    return CustomArgument(LiteralArgument(name)) { info ->
        if (info.sender !is Player) throw error("Player only Command", false)
        val weRegion =
            (info.sender as Player).worldEditSelection() ?: throw error("No World Edit selection made", false)
        weRegion.toRegion(info.sender.location()!!.world)
    }
}

fun regionArgument(key: String): Argument<Location> {
    return LocationArgument("${key}Edge").then(
        CustomArgument(LocationArgument(key)) { info ->
            val prev = info.previousArgs.argsMap()["${key}Edge"] as Location
            val next = info.currentInput
            Region(prev, next)
        }
    )
}

fun combinator(a: Argument<*>, b: Argument<*>) {

}