package de.cypdashuhn.build.commands

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.build.actions.SchematicManager
import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.rooster.commands_new.constructors.*
import de.cypdashuhn.rooster.localization.tSend
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

fun load(argInfo: InvokeInfo, pos1: Location, pos2: Location?) {
    BuildManager.loadAll(argInfo.sender as Player, argInfo.context["build"] as String, pos1, pos2)
}

val load = Arguments.literal.single("load")
    .followedBy(Arguments.list.dbList(
        DbBuildsManager.Build, DbBuildsManager.Builds.name, key = "build",
        errorInvalidMessageKey = "build_not_found",
        errorMissingMessageKey = "build_name_missing",
    )).followedBy(
        Arguments.literal.single("sel").onExecute {
            val loc = BuildManager.selectionCorner(it.sender as Player) ?: run { return@onExecute }
            load(it, loc, null)
        },

    )
    .onExecute {
        val buildName = it.context["build"] as String

        val location = (it.sender as Player).targetBlock() ?: run {
            it.sender.tSend("build_no_block_selected")
            return@onExecute
        }

        SchematicManager.load(buildName, 1, location)
    }


object BuildCommand : RoosterCommand("build") {
    override fun content(arg: UnfinishedArgument): Argument {
        return arg
            .onExecute {
                it.sender.tSend("test")
            }
            .followedBy(register, load)
    }
}

/** Returns the block the Player is currently looking at */
fun Player.targetBlock(): Location? {
    return player!!.getTargetBlockExact(15)?.location
}

