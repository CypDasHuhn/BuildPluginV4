package de.cypdashuhn.build.commands.build

import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.BaseArgument
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.UnfinishedArgument

object BuildCommand : RoosterCommand("!build") {
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg
            .followedBy(create, load, edit, save)

        return command
    }
}