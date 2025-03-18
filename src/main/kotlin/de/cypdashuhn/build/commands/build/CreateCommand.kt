package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster_worldedit.commands.WESelectionArgument
import org.bukkit.entity.Player

object CreateCommand : RoosterCommand("!create", onStart = { it is Player }) {
    private val uniqueNameArgument = Arguments.names.unique(
        key = "name",
        targetColumn = DbBuildsManager.Builds.name,
        uniqueErrorKey = "build.create.name_used_error",
    ).onMissing(playerMessage("build_register_name_missing"))

    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg
            .followedBy(uniqueNameArgument)
            .followedBy(
                listOf(
                    WESelectionArgument.regionSelection(),
                    Arguments.location.region()
                ).eachOnExecuteWithThis { info, self ->
                    val name = info.arg(uniqueNameArgument)
                    val region = info.arg(self)

                    info.sender.tSend("build.create.success", "name" to name)

                    BuildManager.create(info.sender as Player, name, region)
                })
        return command
    }
}