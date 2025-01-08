package de.cypdashuhn.rooster.ui.interfaces

import de.cypdashuhn.rooster.core.Rooster.interfaceContextProvider
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass

/**
 * An Instance of Interface is a model of a UI component. It's main
 * ingredient is [getInterfaceItems], which get resolved dynamically. The
 * field [interfaceName] is the key connected to the particular Interface.
 */
abstract class Interface<T : Context>(
    open val interfaceName: String,
    open val contextClass: KClass<T>,
    val cancelEvent: (ClickInfo<T>) -> Boolean = { true },
    val ignorePlayerInventory: Boolean = true,
    val ignoreEmptySlots: Boolean = true
) {
    val items
        get() = getInterfaceItems()

    abstract fun getInventory(player: Player, context: T): Inventory

    abstract fun getInterfaceItems(): List<InterfaceItem<T>>
    abstract fun defaultContext(player: Player): T
    open fun onClose(player: Player, context: T, event: InventoryCloseEvent) {

    }

    fun openInventory(player: Player, context: T): Inventory {
        return InterfaceManager.openTargetInterface(player, this, context)
    }

    fun getContext(player: Player): T {
        return interfaceContextProvider.getContext(player, this) ?: defaultContext(player)
    }

    fun getCurrentContext(player: Player): T? {
        return interfaceContextProvider.getContext(player, this)
    }
}