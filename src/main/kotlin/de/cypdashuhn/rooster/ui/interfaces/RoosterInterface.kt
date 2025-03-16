package de.cypdashuhn.rooster.ui.interfaces

import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.core.Rooster.interfaceContextProvider
import de.cypdashuhn.rooster.core.hasRoosterIgnore
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
abstract class RoosterInterface<T : Context>(
    open val interfaceName: String,
    open val contextClass: KClass<T>,
    val cancelEvent: (ClickInfo<T>) -> Boolean = { true },
    val ignorePlayerInventory: Boolean = true,
    val ignoreEmptySlots: Boolean = true
) {
    init {
        if (!hasRoosterIgnore(this)) Rooster.registeredInterfaces += this
    }

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

    private val currentInventorySize: MutableMap<Player, Int?> = mutableMapOf()
    private var groupedMapBySize: MutableMap<Int, Map<Slot, List<InterfaceItem<T>>>?> = mutableMapOf()

    internal fun groupedItems(player: Player, context: T): Map<Slot, List<InterfaceItem<T>>> {
        val inventorySize = currentInventorySize[player] ?: getInventory(player, context).size
        val grouping = groupedMapBySize[inventorySize]

        return if (grouping != null) grouping else {
            val map: MutableMap<Slot, MutableList<InterfaceItem<T>>> = mutableMapOf()
            val allSlots = (0..<inventorySize).toList().toTypedArray()
            items.forEach { item ->
                val slots = if (item.slots.all) allSlots else item.slots.slots
                slots.forEach { slot ->
                    map.getOrPut(slot) { mutableListOf() }.add(item)
                }
            }

            groupedMapBySize[inventorySize] = map
            map
        }
    }
}