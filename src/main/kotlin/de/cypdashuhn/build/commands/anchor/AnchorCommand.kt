package de.cypdashuhn.build.commands.build

import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.UnfinishedArgument

object AnchorCommand : RoosterCommand("!anchor") {
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg.followedBy(

        )

        return command
    }

}