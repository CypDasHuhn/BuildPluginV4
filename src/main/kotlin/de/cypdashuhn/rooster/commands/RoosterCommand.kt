package de.cypdashuhn.rooster.commands

import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.core.hasRoosterIgnore
import de.cypdashuhn.rooster.util.isCommandBlock
import de.cypdashuhn.rooster.util.isConsole
import de.cypdashuhn.rooster.util.isMinecart
import de.cypdashuhn.rooster.util.isPlayer
import org.bukkit.command.CommandSender

enum class CommandTarget(val selector: (CommandSender) -> Boolean) {
    ALL({ true }),
    PLAYER({ it.isPlayer() }),
    CONSOLE({ it.isConsole() }),
    BLOCK({ it.isCommandBlock() }),
    MINECART({ it.isMinecart() }),
    IN_GAME({ !it.isConsole() }),
    NON_PLAYER({ !it.isPlayer() }),
    NON_PLAYER_IN_GAME({ it.isMinecart() || it.isCommandBlock() });
}

abstract class RoosterCommand {
    internal val labels: List<String>
    internal val onStart: (CommandSender) -> Boolean
    internal var commandTarget: CommandTarget
    private lateinit var key: String

    init {
        val s = ""
        if (!hasRoosterIgnore(this)) Rooster.registeredCommands += this
    }

    constructor(
        labels: List<String>,
        onStart: (CommandSender) -> Boolean = { true },
        key: String = "label",
        commandTarget: CommandTarget = CommandTarget.ALL
    ) {
        if (labels.isEmpty()) throw IllegalStateException("labels must not be empty")

        this.labels = labels
        this.onStart = onStart
        this.key = key
        this.commandTarget = commandTarget
    }

    constructor(
        label: String,
        onStart: (CommandSender) -> Boolean = { true },
        key: String = "label",
        commandTarget: CommandTarget = CommandTarget.ALL
    ) {
        this.labels = listOf(label)
        this.onStart = onStart
        this.key = key
        this.commandTarget = commandTarget
    }

    internal val command: Argument by lazy { content(Arguments.literal.single(key, isTarget = { true })) }
    abstract fun content(arg: UnfinishedArgument): Argument
}
