package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.commands.wrapper.error
import dev.cypdashuhn.build.commands.wrapper.listEntryArgument
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.IntegerArgument

fun buildNameArgument() = listEntryArgument("build", DbBuildsManager.all()) { it.name }

fun frameArgument(extraFrames: Int) =
    CustomArgument(IntegerArgument("frame")) { info ->
        val build: DbBuildsManager.Build by info.previousArgs.argsMap
        val frameRange = 1..DbBuildsManager.frameCount(build) + extraFrames
        // TODO: Localize
        if (!frameRange.contains(info.currentInput)) throw error("Frame ${info.currentInput} is not between 1 and ${build.frameAmount + 1}")
        info.currentInput
    }.replaceSuggestions(ArgumentSuggestions.strings {
        val build: DbBuildsManager.Build by it.previousArgs.argsMap
        val frameRange = 1..DbBuildsManager.frameCount(build) + extraFrames
        frameRange.map { it.toString() }.toTypedArray()
    })