package de.cypdashuhn.rooster_worldedit.commands

import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster.region.Region
import de.cypdashuhn.rooster_worldedit.adapter.toRegion
import de.cypdashuhn.rooster_worldedit.adapter.worldEditSelection
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object WESelectionArgument {
    fun cornerSelection(
        name: String = "sel",
        key: String = "region",
        /** Use the min corner of the selection, if false, it uses the max corner */
        useMinCorner: Boolean = true,
        /** Error message key if the selection is not present.
         * By default null, meaning without a selection this command isn't enabled */
        selectionNotPresentErrorKey: String? = null
    ): WELocationArgumentType {
        val arg = Arguments.literal.single(
            name = name,
            key = key,
            isEnabled = { (it.sender is Player) && (selectionNotPresentErrorKey != null || it.sender.worldEditSelection() != null) },
            isValid = {
                if (selectionNotPresentErrorKey == null) IsValidResult.Valid()
                else if ((it.sender as Player).worldEditSelection() == null) IsValidResult.Invalid { info ->
                    info.sender.tSend(
                        selectionNotPresentErrorKey
                    )
                }
                else IsValidResult.Valid()
            },
            transformValue = {
                val region = (it.sender as Player).worldEditSelection()!!.toRegion(it.sender)
                if (useMinCorner) region.min else region.max
            }
        )

        return WELocationArgumentType(arg, key)
    }

    fun regionSelection(
        name: String = t("rooster_worldedit.selection"),
        key: String = "region",
        /** Error message key if the selection is not present.
         * By default null, meaning without a selection this command isn't enabled */
        selectionNotPresentErrorKey: String? = null
    ): WERegionArgumentType {
        val arg = Arguments.literal.single(
            name = name,
            key = key,
            isEnabled = { (it.sender is Player) && (selectionNotPresentErrorKey != null || it.sender.worldEditSelection() != null) },
            isValid = {
                if (selectionNotPresentErrorKey == null) IsValidResult.Valid()
                else if ((it.sender as Player).worldEditSelection() == null) IsValidResult.Invalid { info ->
                    info.sender.tSend(
                        selectionNotPresentErrorKey
                    )
                }
                else IsValidResult.Valid()
            },
            transformValue = { (it.sender as Player).worldEditSelection()!!.toRegion(it.sender) }
        )

        return WERegionArgumentType(arg, key)
    }
}

class WERegionArgumentType(arg: UnfinishedArgument, key: String) : TypedArgument<Region>(arg) {
    override fun value(sender: CommandSender, context: CommandContext): TypeResult<Region> {
        val region = context[key] as Region? ?: return TypeResult.Failure(IllegalStateException("Region is null"))
        return TypeResult.Success(region)
    }
}

class WELocationArgumentType(arg: UnfinishedArgument, key: String) : TypedArgument<Location>(arg) {
    override fun value(sender: CommandSender, context: CommandContext): TypeResult<Location> {
        val location = context[key] as Location? ?: return TypeResult.Failure(IllegalStateException("Location is null"))
        return TypeResult.Success(location)
    }
}