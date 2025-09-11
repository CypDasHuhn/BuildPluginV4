package dev.cypdashuhn.build.worldedit

import dev.cypdashuhn.build.commands.wrapper.error
import dev.cypdashuhn.rooster.common.region.Region
import dev.cypdashuhn.rooster.common.util.location
import dev.jorel.commandapi.arguments.*
import org.bukkit.Location
import org.bukkit.entity.Player

fun worldEditRegionArgument(name: String = "selection", hideIfNoSelection: Boolean = false): Argument<Region> {
    return CustomArgument(TextArgument(name)) { info ->
        if (info.input != name) throw error("Invalid selection name", false)
        if (info.sender !is Player) throw error("Player only Command", false)
        val weRegion =
            (info.sender as Player).worldEditSelection() ?: throw error("No World Edit selection made", false)
        weRegion.toRegion(info.sender.location()!!.world)
    }.replaceSuggestions(ArgumentSuggestions.strings {
        if (hideIfNoSelection && (it.sender() as Player).worldEditSelection() == null) arrayOf<String>()
        else arrayOf("selection")
    })
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