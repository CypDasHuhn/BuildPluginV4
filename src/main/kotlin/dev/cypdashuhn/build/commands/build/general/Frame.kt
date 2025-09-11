package dev.cypdashuhn.build.commands.build.general

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.commands.wrapper.eitherOf
import dev.cypdashuhn.build.commands.wrapper.error
import dev.cypdashuhn.build.commands.wrapper.simpleSuggestions
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.entity.Player

const val frameKey = "frame"
fun frameArgument(extraFrames: Int) = CustomArgument(IntegerArgument(frameKey)) { info ->
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

const val dynamicFrameKey = "dynamicFrame"
fun dynamicFrameArgument(): Argument<Int> {
    return CustomArgument(StringArgument(dynamicFrameKey)) { info ->
        val build: DbBuildsManager.Build by info.previousArgs.argsMap
        if (info.input == "-new") {
            DbBuildsManager.frameCount(build) + 1
        } else if (listOf("-last", "-after-last").contains(info.input)) {
            val lastFrame = BuildManager.getLastFrame(info.sender as Player, build)
            if (lastFrame == null) throw error("Last frame too long ago", false)
            if (info.input == "-last") lastFrame else lastFrame + 1
        } else throw error("Invalid frame: ", true)
    }.simpleSuggestions("-last", "-after-last", "-new")
}

fun broaderFrame() = listOf(frameArgument(1), dynamicFrameArgument())
fun CommandArguments.getFrame(): Int = this.eitherOf(frameKey, dynamicFrameKey)