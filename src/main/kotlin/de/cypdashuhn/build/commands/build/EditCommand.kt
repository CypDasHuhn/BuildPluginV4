package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster_worldedit.commands.WESelectionArgument
import org.bukkit.entity.Player

object EditCommand : RoosterCommand("!edit") {
    override fun content(arg: UnfinishedArgument): Argument {
        return arg
            .followedBy(buildNameArgument)
            .followedBy(editRegionArguments.eachFollowedBy(newFrame))
    }

    private val editRegionArguments = listOf(
        WESelectionArgument.regionSelection(),
        Arguments.location.region()
    )

    private val newFrame = listOf(
        Arguments.literal.single(
            key = "frame",
            name = t("build.frame.new_frame"),
            transformValue = { it.arg(buildNameArgument).frameAmount + 1 },
        ).adapt({
            it.toInt()
        }),
        Arguments.list.single(
            key = "frame",
            listFunc = {
                val build = it.arg(buildNameArgument)

                (1..build.frameAmount + 1).toList().map { it.toString() }
            },
            notMatchingError = { info, arg ->
                val build = info.arg(buildNameArgument)
                info.sender.tSend(
                    "build.frame.not_matching_error",
                    "frame" to arg, "frameAmount" to build.frameAmount.toString()
                )
            },
            onMissing = playerMessage("build.frame.missing_error")
        ).adapt({
            it.toInt()
        })
    ).eachOnExecuteWithThis { info, self ->
        val build = info.arg(buildNameArgument)
        val region = info.arg(editRegionArguments)
        val frame = info.arg(self)

        BuildManager.save(info.sender as Player, build, frame, region.edge1, region.edge2)
    }
}
