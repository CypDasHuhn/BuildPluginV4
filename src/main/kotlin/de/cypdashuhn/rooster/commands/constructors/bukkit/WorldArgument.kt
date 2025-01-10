package de.cypdashuhn.rooster.commands.constructors.bukkit

import de.cypdashuhn.rooster.commands.SimpleArgumentType
import de.cypdashuhn.rooster.commands.UnfinishedArgument
import de.cypdashuhn.rooster.commands.constructors.ListArgument
import org.bukkit.Bukkit
import org.bukkit.World

object WorldArgument {
    fun single(
        key: String = "world"
    ): WorldArgumentType {

        val arg = ListArgument.single(
            key = key,
            list = listOf(),
            notMatchingError = { _, _ -> },
            onMissing = {},
        )

        return WorldArgumentType(arg, key)
    }

    class WorldArgumentType(argument: UnfinishedArgument, argKey: String) : SimpleArgumentType<World>("World", argument, argKey)

    fun multiple(
        key: String
    ): WorldListArgumentType {
        val list = Bukkit.getWorlds().map { it.name }.toMutableList()
        list.add("global")

        val arg = ListArgument.chainable(
            key = key,
            list = list,
            notMatchingError = { _, _ -> },
            onMissing = {},
        )

        return WorldListArgumentType(arg, key)
    }
    class WorldListArgumentType(argument: UnfinishedArgument, argKey: String) : SimpleArgumentType<List<World>>("WorldList", argument, argKey)

}