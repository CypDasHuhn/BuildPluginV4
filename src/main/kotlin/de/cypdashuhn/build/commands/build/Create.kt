package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster_worldedit.commands.WESelectionArgument
import org.bukkit.entity.Player


object CreateCommand : RoosterCommand("!create", onStart = { it is Player }) {
    private const val BUILD_NAME_KEY = "name"
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg
            .followedBy(
                Arguments.names.unique(
                    key = BUILD_NAME_KEY,
                    table = DbBuildsManager.Builds,
                    targetColumn = DbBuildsManager.Builds.name,
                    uniqueErrorKey = "build.create.name_used_error"
                )
            ).followedBy(listOf(
                WESelectionArgument.regionSelection(),
                Arguments.location.region()
            ).eachOnExecuteWithThis { info, arg ->
                val name = info.context[BUILD_NAME_KEY] as String
                val region = info.arg(arg)

                info.sender.tSend("build.create.success", "name" to name)

                BuildManager.create(info.sender as Player, name, region)
            })I
            .onMissing(playerMessage("build_register_name_missing"))
        return command
    }
}