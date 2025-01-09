package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.commands.errorMessage
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.entity.Player

const val buildNameKey = "name"
val create = Arguments.literal.single(name = t("build_create"), isEnabled = { it.sender is Player })
    .followedBy(
        Arguments.names.unique(
            key = buildNameKey,
            table = DbBuildsManager.Builds,
            targetColumn = DbBuildsManager.Builds.name,
            uniqueErrorKey = "build_create_name_used"
        )
    ).onExecute {
        val name = it.context[buildNameKey] as String

        it.sender.tSend("build_create_success", "name" to name)

        BuildManager.create(it.sender as Player, name)
    }
    .onMissing(errorMessage("build_register_name_missing"))