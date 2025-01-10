package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.localization.t

val delete = Arguments.literal.single(t("build.delete.label"))
    .followedBy(buildNameArgument).onExecute {
        val build = it.arg(buildNameArgument)
        BuildManager`.delete(it.sender as Player, build)
    }