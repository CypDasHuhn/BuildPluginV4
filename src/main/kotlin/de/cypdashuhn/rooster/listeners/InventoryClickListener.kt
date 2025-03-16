package de.cypdashuhn.rooster.listeners

import de.cypdashuhn.rooster.ui.interfaces.InterfaceManager
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("unused")
object InventoryClickListener : RoosterListener() {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        InterfaceManager.handleInventoryClick(event)
    }
}