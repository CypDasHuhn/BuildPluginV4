package de.cypdashuhn.rooster.ui.items

import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import de.cypdashuhn.rooster.util.appendNumber
import de.cypdashuhn.rooster.util.infix_gate.and
import org.bukkit.inventory.ItemStack

open class InterfaceItem<T : Context> {
    val anonymousKey = "anonymous"
    val conditionMap: MutableMap<String, (InterfaceInfo<T>) -> Boolean>

    fun addCondition(condition: (InterfaceInfo<T>) -> Boolean, name: String = anonymousKey) {
        val newName = if (conditionMap.contains(name)) appendNumber(name) else name
        conditionMap[newName] = condition
    }

    fun removeCondition(name: String? = null) {
        if (name == null) {
            conditionMap.clear()
        } else {
            conditionMap.remove(name)
        }
    }

    fun setCondition(condition: (InterfaceInfo<T>) -> Boolean, name: String? = null) {
        if (name == null) {
            conditionMap.clear()
            conditionMap[anonymousKey] = condition
        } else {
            conditionMap[name] = condition
        }
    }

    /** Returns true if the condition was found, false if not. */
    fun overrideCondition(
        name: String = anonymousKey,
        transformedCondition: ((InterfaceInfo<T>) -> Boolean) -> (InterfaceInfo<T>) -> Boolean
    ): Boolean {
        return if (conditionMap.contains(name)) {
            conditionMap[name] = transformedCondition(conditionMap[name]!!)
            true
        } else false
    }

    val totalCondition: (InterfaceInfo<T>) -> Boolean
        get() {
            return conditionMap.values.reduce { acc, condition -> acc and condition }
        }
    var itemStackCreator: (InterfaceInfo<T>) -> ItemStack
    var priority: (InterfaceInfo<T>) -> Int
    var action: (ClickInfo<T>) -> Unit
    var slots: Slots

    constructor(
        conditionMap: Map<String, (InterfaceInfo<T>) -> Boolean>,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        this.conditionMap = conditionMap.toMutableMap()
        this.itemStackCreator = itemStackCreator
        this.priority = priority
        this.action = action
        this.slots = Slots.all()
    }

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition)
        this.itemStackCreator = itemStackCreator
        this.priority = priority
        this.action = action
        this.slots = Slots.all()
    }

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        action: (ClickInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition)
        this.itemStackCreator = { itemStack }
        this.priority = priority
        this.action = action
        this.slots = Slots.all()
    }

    constructor(
        slots: Slots,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition)
        this.itemStackCreator = itemStackCreator
        this.priority = priority
        this.action = action
        this.slots = slots
    }

    constructor(
        slots: Slots,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        action: (ClickInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition)
        this.itemStackCreator = { itemStack }
        this.priority = priority
        this.action = action
        this.slots = slots
    }

    constructor(
        slots: Slots,
        conditionMap: Map<String, (InterfaceInfo<T>) -> Boolean>,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        this.conditionMap = conditionMap.toMutableMap()
        this.itemStackCreator = itemStackCreator
        this.priority = priority
        this.action = action
        this.slots = slots
    }
}