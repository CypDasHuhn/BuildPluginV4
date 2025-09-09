package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.actions.BuildManager
import dev.cypdashuhn.build.commands.wrapper.error
import dev.cypdashuhn.build.commands.wrapper.listArgument
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.jorel.commandapi.StringTooltip
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun buildNameArgument() = listArgument("build", { DbBuildsManager.all() }, { name }) {
    StringTooltip.ofString(
        name,
        "$readableDimensions | frames: $frameAmount"
    )
}

const val frameKey = "frame"
fun frameArgumentWithoutSuggestions(extraFrames: Int) = CustomArgument(IntegerArgument(frameKey)) { info ->
    val build: DbBuildsManager.Build by info.previousArgs.argsMap
    val frameRange = 1..DbBuildsManager.frameCount(build) + extraFrames
    // TODO: Localize
    if (!frameRange.contains(info.currentInput)) throw error("Frame ${info.currentInput} is not between 1 and ${build.frameAmount + 1}")
    info.currentInput
}

fun frameArgumentSuggestions(extraFrames: Int) = ArgumentSuggestions.strings<CommandSender> {
    val build: DbBuildsManager.Build by it.previousArgs.argsMap
    val frameRange = 1..DbBuildsManager.frameCount(build) + extraFrames
    frameRange.map { it.toString() }.toTypedArray()
}

fun frameArgument(extraFrames: Int) =
    frameArgumentWithoutSuggestions(extraFrames).replaceSuggestions(frameArgumentSuggestions(extraFrames))

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
    }.replaceSuggestions(dynamicFrameArgumentSuggestions())
}

fun dynamicFrameArgumentSuggestions() =
    ArgumentSuggestions.strings<CommandSender> { listOf("-last", "-after-last", "-new").toTypedArray() }

fun CommandArguments.getFrame(): Int {
    return this.argsMap[frameKey] as Int? ?: this.argsMap[dynamicFrameKey] as Int
}