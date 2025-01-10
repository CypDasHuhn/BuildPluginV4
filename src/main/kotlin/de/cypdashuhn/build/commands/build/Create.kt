package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.commands.playerMessage
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.entity.Player

const val buildNameKey = "name"
val create = Arguments.literal.single(name = t("build.create.label"), isEnabled = { it.sender is Player })
    .followedBy(
        Arguments.names.unique(
            key = buildNameKey,
            table = DbBuildsManager.Builds,
            targetColumn = DbBuildsManager.Builds.name,
            uniqueErrorKey = "build.create.name_used"
        )
    ).onExecute {
        val name = it.context[buildNameKey] as String

        it.sender.tSend("build.create.success", "name" to name)

        BuildManager.create(it.sender as Player, name)
    }
    .onMissing(playerMessage("build_register_name_missing"))