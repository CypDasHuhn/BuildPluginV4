package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster_worldedit.commands.WESelectionArgument
import org.bukkit.Location
import org.bukkit.entity.Player

val loadRegionArguments = listOf(
    WESelectionArgument.regionSelection(),
    Arguments.location.region()
).eachOnExecuteWithThisUnfinished { info, arg ->
    val region = info.arg(arg)
    load(info, null, region.edge1, region.edge2)
}

val locationArgument = (Arguments.location.location().onExecuteWithThis { info, arg ->
    val loc = info.arg(arg)
    load(info, null, loc, null)
})

fun load(info: InvokeInfo, frameNum: Int?, pos1: Location, pos2: Location?) {
    val build = info.arg(buildNameArgument)
    val player = info.sender as Player
    if (frameNum != null) BuildManager.load(player, build, frameNum, pos1, pos2)
    else BuildManager.loadAll(player, build, pos1, pos2)
}

val loadFrameArgument = frameArgument.onExecute {
    val loc = it.argNullable(locationArgument)
    val frame = it.context["frame"] as Double

    if (loc != null) {
        load(it, frame.toInt(), loc, null)
    } else {
        val region = it.arg(loadRegionArguments)
        load(it, frame.toInt(), region.edge1, region.edge2)
    }
}

val load: Argument = Arguments.literal.single("load")
    .followedBy(buildNameArgument)
    .followedBy((loadRegionArguments or locationArgument).eachFollowedBy(loadFrameArgument))