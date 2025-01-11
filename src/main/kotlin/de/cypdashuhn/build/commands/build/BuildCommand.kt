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
    errorInvalidMessageKey = "build.build_not_found_error",
    errorMissingMessageKey = "build.name_missing_error",
)

val frameArgument = Arguments.number.integer(
    key = "frame",
    negativeRule = ArgumentRule.create("build.frame.at_least_one_error"),
    zeroRule = ArgumentRule.create("build.frame.at_least_one_error"),
    tabCompleterPlaceholder = "build.frame.placeholder",
    onMissing = playerMessage("build.frame.missing_error")
)