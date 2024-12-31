package de.cypdashuhn.rooster.commands_new.utility_constructors.bukkit

import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.ListArgument
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.Bukkit

object PlayerArgument {

    fun single(
        key: String = "player",
        notMatchingKey: String = "player_not_matching",
        notMatchingArg: String = "player",
        onMissingKey: String = "player_missing",
    ): UnfinishedArgument {
        return ListArgument.single(
            key = key,
            list = Bukkit.getOnlinePlayers().map { it.name },
            notMatchingError = { info, player -> info.sender.tSend(notMatchingKey, notMatchingArg to player) },
            onMissing = { it.sender.tSend(onMissingKey) },
        )
    }

    fun multiple(
        key: String = "player",
        notMatchingKey: String = "player_not_matching",
        notMatchingArg: String = "player",
        onMissingKey: String = "player_missing",
    ): UnfinishedArgument {
        return ListArgument.chainable(
            key = key,
            list = Bukkit.getOnlinePlayers().map { it.name },
            notMatchingError = { info, player -> info.sender.tSend(notMatchingKey, notMatchingArg to player) },
            onMissing = { it.sender.tSend(onMissingKey) },
        )
    }
}