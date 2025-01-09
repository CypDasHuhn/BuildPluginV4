package de.cypdashuhn.build.commands.build

import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.rooster.commands.*

object BuildCommand : RoosterCommand("!build") {
    override fun content(arg: UnfinishedArgument): Argument {
        val command = arg
            .followedBy(create, edit, load)

        return command
    }
}

val buildNameArgument = Arguments.list.dbList(
    DbBuildsManager.Build,
    DbBuildsManager.Builds.name,
    key = "build",
    errorInvalidMessageKey = "build.build.not_found",
    errorMissingMessageKey = "build.name.missing",
)

val frameArgument = Arguments.number.integer(
    key = "frame",
    negativeRule = ArgumentRule.create("build.frame.at_least_one"),
    zeroRule = ArgumentRule.create("build.frame.at_least_one"),
    tabCompleterPlaceholder = "build.frame.placeholder",
    onMissing = errorMessage("build.frame.missing")
)