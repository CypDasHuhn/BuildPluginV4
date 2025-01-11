package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster_worldedit.commands.WESelectionArgument
import org.bukkit.entity.Player

val editRegionArguments = listOf(
    WESelectionArgument.regionSelection(),
    Arguments.location.region()
)

val newFrame = listOf(
    Arguments.literal.single(
        key = "frame",
        name = t("build.frame.new_frame"),
        transformValue = { it.arg(buildNameArgument).frameAmount + 1 },
    ).adapt({ it.toInt() }),
    Arguments.list.single(
        key = "frame",
        listFunc = {
            val build = it.arg(buildNameArgument)

            (1..build.frameAmount).toList().map { it.toString() }
        },
        notMatchingError = playerMessageExtra("build.frame.not_matching", "frame"),
        onMissing = playerMessage("build.frame.missing")
    ).adapt({ it.toInt() })
).eachOnExecuteWithThis { info, self ->
    val build = info.arg(buildNameArgument)
    val region = info.arg(editRegionArguments)
    val frame = info.arg(self)

    BuildManager.save(info.sender as Player, build, frame, region.edge1, region.edge2)
}

val edit: Argument = Arguments.literal.single("save")
    .followedBy(buildNameArgument)
    .followedBy(editRegionArguments.eachFollowedBy(newFrame))
