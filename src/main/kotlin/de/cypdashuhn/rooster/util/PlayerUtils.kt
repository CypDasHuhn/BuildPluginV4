package de.cypdashuhn.rooster.util

import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.minecart.CommandMinecart

fun Player.uuid() = this.uniqueId.toString()

fun CommandSender.uniqueKey(): String {
    return when (this) {
        is Player -> this.uniqueId.toString()
        is ConsoleCommandSender -> "console"
        is BlockCommandSender -> "${this.block.location.toVector()}"
        else -> "unknown-${this::class.simpleName}"
    }
}

fun CommandSender.location(): Location? {
    return when (this) {
        is Player -> this.location
        is BlockCommandSender -> this.block.location
        is CommandMinecart -> this.location
        else -> null
    }
}