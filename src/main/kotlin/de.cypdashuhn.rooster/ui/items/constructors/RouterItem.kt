package de.cypdashuhn.rooster.ui.items.constructors

import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import de.cypdashuhn.rooster.ui.items.Condition
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.ui.items.ItemStackCreator
import org.bukkit.inventory.ItemStack

open class RouterItem<T : Context, K : Context> : InterfaceItem<T> {
    protected companion object {
        fun <T : Context, K : Context> routerAction(
            furtherAction: (ClickInfo<T>) -> Unit,
            context: ((ClickInfo<T>) -> K?)?,
            targetInterface: Interface<K>
        ): (ClickInfo<T>) -> Unit {
            return { clickInfo ->
                furtherAction(clickInfo)
                var usedContext: K? = context?.let { it(clickInfo) }
                if (usedContext == null) usedContext = targetInterface.getContext(clickInfo.click.player)
                targetInterface.openInventory(clickInfo.click.player, usedContext)
            }

        }
    }

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        targetInterface: Interface<K>,
        context: ((ClickInfo<T>) -> K?)? = null,
        furtherAction: (ClickInfo<T>) -> Unit = {},
    ) : super(condition, itemStackCreator, routerAction(furtherAction, context, targetInterface))

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        targetInterface: Interface<K>,
        context: ((ClickInfo<T>) -> K?)? = null,
        furtherAction: (ClickInfo<T>) -> Unit = {},
    ) : super(condition, itemStack, routerAction(furtherAction, context, targetInterface))

    constructor(
        condition: Condition<T>,
        itemStack: ItemStackCreator<T>,
        targetInterface: Interface<K>,
        context: ((ClickInfo<T>) -> K?)? = null,
        furtherAction: (ClickInfo<T>) -> Unit = {},
    ) : super(condition(), itemStack(), routerAction(furtherAction, context, targetInterface))
}

