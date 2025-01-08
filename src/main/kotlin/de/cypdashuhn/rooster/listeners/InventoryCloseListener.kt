package de.cypdashuhn.rooster.listeners

import de.cypdashuhn.rooster.core.Rooster.cache
import de.cypdashuhn.rooster.ui.interfaces.InterfaceManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

@Suppress("unused")
@RoosterListener
object InventoryCloseListener : Listener {
    @EventHandler
    fun listener(event: InventoryCloseEvent) {
        if (cache.getIfPresent(InterfaceManager.CHANGES_INTERFACE_KEY, event.player) as Boolean? == true) {
            cache.invalidate(InterfaceManager.CHANGES_INTERFACE_KEY, event.player)
            return
        }
        InterfaceManager.closeInterface(event.player as Player, event)
    }
}