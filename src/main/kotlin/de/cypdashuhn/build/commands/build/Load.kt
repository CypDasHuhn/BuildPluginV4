package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster_worldedit.commands.WESelectionArgument
import org.bukkit.Location
import org.bukkit.entity.Player

object LoadCommand : RoosterCommand("!load") {
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg

        return command.followedBy(buildNameArgument)
            .followedBy((regionArguments or locationArgument).eachFollowedBy(loadFrameArgument))
    }

    private val regionArguments = listOf(
        WESelectionArgument.regionSelection(),
        Arguments.location.region()
    ).eachOnExecuteWithThisUnfinished { info, arg ->
        val region = info.arg(arg)
        load(info, null, region.edge1, region.edge2)
    }

    private val locationArgument = (Arguments.location.location().onExecuteWithThis { info, arg ->
        val loc = info.arg(arg)
        load(info, null, loc, null)
    })

    private fun load(info: InvokeInfo, frameNum: Int?, pos1: Location, pos2: Location?) {
        val build = info.arg(buildNameArgument)
        val player = info.sender as Player
        if (frameNum != null) BuildManager.load(player, build, frameNum, pos1, pos2)
        else BuildManager.loadAll(player, build, pos1, pos2)
    }

    private val loadFrameArgument = frameArgument.copy().onExecuteWithThisFinished { info, self: TypedArgument<Int> ->
        val loc = info.argNullable(locationArgument)
        val frame = info.arg(self)

        if (loc != null) {
            load(info, frame, loc, null)
        } else {
            val region = info.arg(regionArguments)
            load(info, frame, region.edge1, region.edge2)
        }
    }
}