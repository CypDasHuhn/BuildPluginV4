package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.listeners.chat.ChatManager.chatConfirmation
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.entity.Player

val delete = Arguments.literal.single(t("build.delete.label"))
    .followedBy(buildNameArgument).onExecute {
        val build = it.arg(buildNameArgument)

        (it.sender as Player).chatConfirmation(
            onConfirm = {
                BuildManager.delete(build)
            },
            onCancel = {
                it.tSend("build.delete.cancelled")
            }
        )
    }