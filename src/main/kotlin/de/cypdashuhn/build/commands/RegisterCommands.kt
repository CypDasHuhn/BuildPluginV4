package de.cypdashuhn.build.commands

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster_worldedit.commands.WESelectionArgument
import org.bukkit.Location
import org.bukkit.entity.Player

val register = Arguments.literal.single(name = "register", isEnabled = { it.sender is Player })
    .followedBy(Arguments.names.unique(table = DbBuildsManager.Builds, targetColumn = DbBuildsManager.Builds.name))
    .onExecute {
        val name = it.context["name"] as String

        it.sender.tSend("build_register_step_1", "name" to name)

        BuildManager.create(it.sender as Player, name)
    }
    .onMissing(errorMessage("build_register_name_missing"))

fun load(argInfo: InvokeInfo, pos1: Location, pos2: Location?, frame: Int? = null) {
    val player = argInfo.sender as Player
    val build = argInfo.context["build"] as String

    if (frame == null) BuildManager.loadAll(player, build, pos1, pos2)
    else BuildManager.load(player, build, frame, pos1, pos2)
}

val buildNameArgument = Arguments.list.dbList(
    DbBuildsManager.Build,
    DbBuildsManager.Builds.name,
    key = "build",
    errorInvalidMessageKey = "build_not_found",
    errorMissingMessageKey = "build_name_missing",
)

val regionArguments = listOf(
    WESelectionArgument.regionSelection(),
    Arguments.location.region()
).eachOnExecuteWithThis { info, arg ->
    val region = info.arg(arg)
    load(info, region.edge1, region.edge2)
}

val locationArgument = (Arguments.location.location().onExecuteWithThis { info, arg ->
    val loc = info.arg(arg)
    load(info, loc, null)
})

val frameArgument = Arguments.number.number(
    key = "frame",
    negativesNotAcceptedErrorMessageKey = "build_frame_not_positive"
).onExecute {
    val loc = it.argNullable(locationArgument)
    val frame = it.context["frame"] as Double

    if (loc != null) {
        load(it, loc, null, frame.toInt())
    } else {
        val region = it.arg(regionArguments)
        load(it, region.edge1, region.edge2, frame.toInt())
    }
}

val load: Argument = Arguments.literal.single("load")
    .followedBy(buildNameArgument)
    .followedBy((regionArguments or locationArgument).eachFollowedBy(frameArgument))

val selection = WESelectionArgument.regionSelection()

val newFrame = Arguments.number.number(
    key = "frame",
    negativesNotAcceptedErrorMessageKey = "build_frame_not_positive"
).onExecute {
    val buildName = it.context["build"] as String
    val region = it.arg(selection)
    val frame = it.context["frame"] as Double

    BuildManager.save(it.sender as Player, buildName, frame.toInt(), region.edge1, region.edge2)
}

val save: Argument = Arguments.literal.single("save")
    .followedBy(buildNameArgument)
    .followedBy(selection)
    .followedBy(newFrame)


object BuildCommand : RoosterCommand("build") {
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg
            .followedBy(register, load, save)
        
        return command
    }
}

/** Returns the block the Player is currently looking at */
fun Player.targetBlock(): Location? {
    return player!!.getTargetBlockExact(15)?.location
}

