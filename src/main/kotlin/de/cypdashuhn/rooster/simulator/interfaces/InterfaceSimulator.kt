package de.cypdashuhn.rooster.simulator.interfaces

import de.cypdashuhn.rooster.simulator.Simulator
import de.cypdashuhn.rooster.ui.interfaces.Click
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.InterfaceManager
import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

object InterfaceSimulator {

    fun <T : Context> openInterface(targetInterface: RoosterInterface<T>, context: T? = null): Inventory {
        requireNotNull(Simulator.player) { "Simulator not initialized" }
        return targetInterface.openInventory(
            Simulator.player!!,
            context ?: targetInterface.getContext(Simulator.player!!)
        )
    }

    fun click(slot: Int, clickType: ClickType): Inventory {
        val event = InventoryClickEvent(
            Simulator.player!!.openInventory,
            InventoryType.SlotType.CONTAINER,
            slot,
            clickType,
            InventoryAction.NOTHING
        )
        val item = Simulator.currentInventory!!.getItem(slot)
        val click = Click(event, Simulator.player!!, item, item?.type, event.slot)
        InterfaceManager.click(click, event, Simulator.currentInterface!!, Simulator.player!!)
        return Simulator.currentInventory!! // This is manipulated if the click opens a new inventory for a player.
    }

}