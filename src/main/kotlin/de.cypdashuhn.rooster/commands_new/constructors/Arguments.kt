package de.cypdashuhn.rooster.commands_new.constructors

import de.cypdashuhn.rooster.commands_new.utility_constructors.ListArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.LiteralArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.NameArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.NumberArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.bukkit.LocationArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.bukkit.MaterialArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.bukkit.PlayerArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.bukkit.WorldArgument

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