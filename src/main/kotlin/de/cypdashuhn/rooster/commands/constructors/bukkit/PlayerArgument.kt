package de.cypdashuhn.rooster.commands.constructors.bukkit

import de.cypdashuhn.rooster.commands.SimpleArgumentType
import de.cypdashuhn.rooster.commands.UnfinishedArgument
import de.cypdashuhn.rooster.commands.constructors.ListArgument
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlayerArgument {
    fun single(
        key: String = "player",
        notMatchingKey: String = "rooster.player.not_matching_error",
        notMatchingArg: String = "player",
        onMissingKey: String = "rooster.player.missing_error",
    ): UnfinishedArgument {
        return ListArgument.single(
            key = key,
            list = Bukkit.getOnlinePlayers().map { it.name },
            notMatchingError = { info, player -> info.sender.tSend(notMatchingKey, notMatchingArg to player) },
            onMissing = { it.sender.tSend(onMissingKey) },
        )
    }

    class PlayerArgumentType(argument: UnfinishedArgument, argKey: String) :
        SimpleArgumentType<Player>("Player", argument, argKey)

    fun multiple(
        key: String = "player",
        notMatchingKey: String = "rooster.player.not_matching_error",
        notMatchingArg: String = "player",
        onMissingKey: String = "rooster.player.missing_error",
    ): UnfinishedArgument {
        return ListArgument.chainable(
            key = key,
            list = Bukkit.getOnlinePlayers().map { it.name },
            notMatchingError = { info, player -> info.sender.tSend(notMatchingKey, notMatchingArg to player) },
            onMissing = { it.sender.tSend(onMissingKey) },
        )
    }

    class PlayerListArgumentType(argument: UnfinishedArgument, argKey: String) :
        SimpleArgumentType<List<Player>>("PlayerList", argument, argKey)
}