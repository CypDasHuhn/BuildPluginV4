package de.cypdashuhn.build.commands.settings

import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.UnfinishedArgument
import de.cypdashuhn.rooster.commands.constructors.LocalizationArgument

object SettingsCommand : RoosterCommand("!settings") {
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg
            .followedBy(
                LocalizationArgument.full()
            )

        return command
    }
}