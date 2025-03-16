package de.cypdashuhn.rooster.ui.items.constructors

import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import de.cypdashuhn.rooster.ui.items.Condition
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.ui.items.ItemStackCreator
import de.cypdashuhn.rooster.util.infix_gate.and
import org.bukkit.inventory.ItemStack

open class PlaceholderItem<T : Context> : InterfaceItem<T> {
    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: (InterfaceInfo<T>) -> ItemStack,
        priority: (InterfaceInfo<T>) -> Int = { -1 }
    ) : super(condition = condition, itemStackCreator = itemStack, priority = priority)

    constructor(
        condition: Condition<T>,
        itemStack: (InterfaceInfo<T>) -> ItemStack,
        priority: (InterfaceInfo<T>) -> Int = { -1 }
    ) : super(condition = condition(), itemStackCreator = itemStack, priority = priority)

    constructor(
        condition: Condition<T>,
        itemStack: ItemStackCreator<T>,
        priority: (InterfaceInfo<T>) -> Int = { -1 }
    ) : super(condition = condition(), itemStackCreator = itemStack(), priority = priority)

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStackCreator<T>
    ) : super(condition = condition, itemStackCreator = itemStack())

    constructor(
        slots: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        priority: (InterfaceInfo<T>) -> Int = { -1 }
    ) : super(
        condition = { it: InterfaceInfo<T> -> it.slot in slots } and condition,
        itemStackCreator = { itemStack },
        priority = priority
    )

    /* Constructors yet needed:
    * slots: IntRange, itemStack: ItemStackCreator<T>
    *
     * */
}