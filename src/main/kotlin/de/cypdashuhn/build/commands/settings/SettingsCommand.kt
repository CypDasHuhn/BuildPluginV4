package de.cypdashuhn.build.commands.settings

import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.UnfinishedArgument

object SettingsCommand : RoosterCommand("!settings") {
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg
            .followedBy(language)

        return command
    }
}