package de.cypdashuhn.rooster.commands_new.utility_constructors.bukkit

import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.ListArgument
import org.bukkit.Bukkit

object WorldArgument {
    fun single(
        key: String = "world"
    ): UnfinishedArgument {
        val list = Bukkit.getWorlds().map { it.name }.toMutableList()
        list.add("global")

        return ListArgument.single(
            key = key,
            list = list,
            notMatchingError = { _, _ -> },
            onMissing = {},
        )
    }

    fun multiple(
        key: String
    ): UnfinishedArgument {
        val list = Bukkit.getWorlds().map { it.name }.toMutableList()
        list.add("global")

        return ListArgument.chainable(
            key = key,
            list = list,
            notMatchingError = { _, _ -> },
            onMissing = {},
        )
    }
}