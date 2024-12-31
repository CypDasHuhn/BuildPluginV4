package de.cypdashuhn.build.commands

import de.cypdashuhn.build.actions.RegisterBuild
import de.cypdashuhn.build.actions.SaveFrame
import de.cypdashuhn.build.db.BuildsManager
import de.cypdashuhn.rooster.commands_new.constructors.*
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.Location
import org.bukkit.entity.Player

val register = Arguments.literal.single(name = "register", isEnabled = { it.sender is Player })
    .followedBy(
        Arguments.literal.single(
            name = "start",
            isEnabled = { !RegisterBuild.isPlayerRegistering(it.sender as Player) }
        )
            .followedBy(Arguments.names.unique(table = BuildsManager.Builds, targetColumn = BuildsManager.Builds.name))
            .onExecute {
                val name = it.context["name"] as String

                it.sender.tSend("build_register_step_1", "name" to name)
            }
            .onMissing(errorMessage("build_register_name_missing")),
        Arguments.literal.single("pos1", isEnabled = { RegisterBuild.isPlayerRegistering(it.sender as Player) })
            .onExecute {
                val location = (it.sender as Player).targetBlock() ?: run {
                    it.sender.tSend("build_register_no_block_selected")
                    return@onExecute
                }
                RegisterBuild.registerPos1(it.sender, location)
            },
        Arguments.literal.single("pos2", isEnabled = { RegisterBuild.isPlayerRegistering(it.sender as Player) })
            .onExecute {
                val location = (it.sender as Player).targetBlock() ?: run {
                    it.sender.tSend("build_register_no_block_selected")
                    return@onExecute
                }
                RegisterBuild.registerPos2(it.sender, location)
            },
        Arguments.literal.single(
            "end",
            isEnabled = { RegisterBuild.isPlayerRegistering(it.sender as Player) && RegisterBuild.playerHasBothPos(it.sender) })
            .onExecute { RegisterBuild.registerEnd(it.sender as Player) }
    )

val test = Arguments.literal.single("test")
    .followedBy(
        Arguments.literal.single("save")
            .followedBy(Arguments.number.xyzCoordinates(keyPreset = "first"))
            .followedBy(Arguments.number.xyzCoordinates(keyPreset = "second"))
            .onExecute {
                val first = Arguments.number.locationFromContext(it, keyPreset = "first")
                val second = Arguments.number.locationFromContext(it, keyPreset = "second")

                assert(first != null && second != null) { "locations should not be null" }
                SaveFrame.save("test", first!!, second!!)
            },
        Arguments.literal.single("load")
            .followedBy(Arguments.number.xyzCoordinates(keyPreset = "first"))
            .onExecute {
                val first = Arguments.number.locationFromContext(it, keyPreset = "first")

                assert(first != null) { "locations should not be null" }
                SaveFrame.load("test", first!!)
            }
    )


object BuildCommand : RoosterCommand("build") {
    override fun content(arg: UnfinishedArgument): Argument {
        return arg
            .onExecute {
                it.sender.tSend("test")
            }
            .followedBy(register, test)
    }
}

/** Returns the block the Player is currently looking at */
fun Player.targetBlock(): Location? {
    return player!!.getTargetBlockExact(15)?.location
}

