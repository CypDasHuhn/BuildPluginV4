package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.UnfinishedArgument
import de.cypdashuhn.rooster.listeners.chat.ChatManager.chatConfirmation
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.entity.Player

object DeleteCommand : RoosterCommand("!delete", onStart = { it is Player }) {
    private const val BUILD_NAME_KEY = "name"
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg
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

        return command
    }
}