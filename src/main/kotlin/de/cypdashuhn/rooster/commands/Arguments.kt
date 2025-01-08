package de.cypdashuhn.rooster.commands

import de.cypdashuhn.rooster.commands.constructors.ListArgument
import de.cypdashuhn.rooster.commands.constructors.LiteralArgument
import de.cypdashuhn.rooster.commands.constructors.NameArgument
import de.cypdashuhn.rooster.commands.constructors.NumberArgument
import de.cypdashuhn.rooster.commands.constructors.bukkit.LocationArgument
import de.cypdashuhn.rooster.commands.constructors.bukkit.MaterialArgument
import de.cypdashuhn.rooster.commands.constructors.bukkit.PlayerArgument
import de.cypdashuhn.rooster.commands.constructors.bukkit.WorldArgument

object Arguments {
    val literal = LiteralArgument
    val number = NumberArgument
    val list = ListArgument
    val names = NameArgument

    val material = MaterialArgument
    val world = WorldArgument
    val player = PlayerArgument
    val location = LocationArgument
}