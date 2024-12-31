package de.cypdashuhn.rooster.ui.items

import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import de.cypdashuhn.rooster.ui.interfaces.Slot
import de.cypdashuhn.rooster.util.createItem
import de.cypdashuhn.rooster.util.infix_gate.and
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Condition<T : Context> {
    var condition: (InterfaceInfo<T>) -> Boolean = { true }

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean
    ) {
        this.condition = condition
    }

    constructor(int: Slot, condition: (InterfaceInfo<T>) -> Boolean = { true }) {
        this.condition = { it: InterfaceInfo<T> -> it.slot == int && condition(it) } and condition
    }

    constructor(int: IntRange, condition: (InterfaceInfo<T>) -> Boolean = { true }) {
        this.condition = { it: InterfaceInfo<T> -> it.slot in int && condition(it) } and condition
    }

    operator fun invoke(): (InterfaceInfo<T>) -> Boolean {
        return condition
    }
}

class ItemStackCreator<T : Context> {
    var itemStackCreator: (InterfaceInfo<T>) -> ItemStack

    constructor(itemStackCreator: (InterfaceInfo<T>) -> ItemStack) {
        this.itemStackCreator = itemStackCreator
    }

    constructor(itemStack: ItemStack) {
        this.itemStackCreator = { itemStack }
    }

    constructor(material: Material, name: String? = null) {
        this.itemStackCreator = { createItem(material, name?.let { Component.text(it) }) }
    }

    operator fun invoke(): (InterfaceInfo<T>) -> ItemStack {
        return itemStackCreator
    }
}