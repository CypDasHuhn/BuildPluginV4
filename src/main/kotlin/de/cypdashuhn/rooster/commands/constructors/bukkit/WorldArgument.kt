package de.cypdashuhn.rooster.commands.constructors.bukkit

import de.cypdashuhn.rooster.commands.UnfinishedArgument
import de.cypdashuhn.rooster.commands.constructors.ListArgument
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