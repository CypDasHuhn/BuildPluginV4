package de.cypdashuhn.rooster.demo

import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.UnfinishedArgument
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.entity.Player

object TestInterfacesCommand : RoosterCommand("testInterfaces") {
    const val INTERFACE_KEY = "interface"

    override fun content(arg: UnfinishedArgument): Argument {
        return arg.followedBy(
            Arguments.list.single(
                key = INTERFACE_KEY,
                list = Rooster.registeredInterfaces.map { it.interfaceName },
                notMatchingError = { info, name -> info.sender.sendMessage("No such interface $name") },
                onMissing = { info -> info.sender.sendMessage("Please specify an interface") },
                transformValue = { _, name -> Rooster.registeredInterfaces.first { it.interfaceName == name } }
            ).onExecute {
                val targetInterface = it.context[INTERFACE_KEY] as RoosterInterface<Context>
                val context = targetInterface.getContext(it.sender as Player)
                targetInterface.openInventory(it.sender as Player, context)
            }
        )
    }
}