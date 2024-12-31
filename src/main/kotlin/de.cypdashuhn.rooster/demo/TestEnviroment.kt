package de.cypdashuhn.rooster.demo

import de.cypdashuhn.rooster.commands_new.constructors.*
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import org.bukkit.entity.Player

const val INTERFACE_KEY = "interface"

object TestInterfaces : RoosterCommand("testInterfaces", onStart = { it is Player }) {
    override fun content(arg: UnfinishedArgument): Argument {
        return arg
            .followedBy(Arguments.list.single(
                key = INTERFACE_KEY,
                list = Rooster.registeredInterfaces.map { it.interfaceName },
                notMatchingError = { info, arg -> errorMessage("rooster.interface_not_found") },
                onMissing = errorMessage("rooster.interface_missing"),
                transformValue = { _, arg ->
                    Rooster.registeredInterfaces.first { it.interfaceName == arg }
                }
            )).onExecute {
                val targetInterface = it.context[INTERFACE_KEY] as Interface<Context>
                val context = targetInterface.getContext(it.sender as Player)
                targetInterface.openInventory(it.sender, context)
            }
    }
}