package de.cypdashuhn.rooster.commands

import org.bukkit.command.CommandSender

abstract class RoosterCommand {
    internal val labels: List<String>
    internal val onStart: (CommandSender) -> Boolean
    private lateinit var key: String

    constructor(
        labels: List<String>,
        onStart: (CommandSender) -> Boolean = { true },
        key: String = "label"
    ) {
        if (labels.isEmpty()) throw IllegalStateException("labels must not be empty")

        this.labels = labels
        this.onStart = onStart
        this.key = key
    }

    constructor(
        label: String,
        onStart: (CommandSender) -> Boolean = { true },
        key: String = "label"
    ) {
        this.labels = listOf(label)
        this.onStart = onStart
        this.key = key
    }

    internal val command: Argument by lazy { content(Arguments.literal.single(key, isTarget = { true })) }
    abstract fun content(arg: UnfinishedArgument): Argument
}
