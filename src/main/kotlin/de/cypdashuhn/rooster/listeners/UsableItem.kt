package de.cypdashuhn.rooster.listeners

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/** Everything here is unfinished. Ignore! */

typealias IsShift = Boolean
typealias IsLeft = Boolean

enum class ClickState(
    val isShift: IsShift?,
    val isLeft: IsLeft?
) {
    CLICK(null, null),
    SHIFT_CLICK(true, null),
    NORMAL_CLICK(false, null),
    LEFT_CLICK(null, true),
    RIGHT_CLICK(null, false),
    LEFT_SHIFT_CLICK(true, true),
    RIGHT_SHIFT_CLICK(true, false),
    LEFT_NORMAL_CLICK(false, true),
    RIGHT_NORMAL_CLICK(false, false)
}

private fun eventHasClicks(clickStates: Array<out ClickState>, isLeft: Boolean, isShift: Boolean): Boolean {
    for (clickState in clickStates) {
        if ((clickState.isShift == null || clickState.isShift == isShift) &&
            (clickState.isLeft == null || clickState.isLeft == isLeft)
        ) {
            return true
        }
    }
    return false
}

fun PlayerInteractEvent.hasClicks(vararg clickStates: ClickState): Boolean {
    return eventHasClicks(clickStates, this.action.isLeftClick, this.player.isSneaking)
}

fun InventoryClickEvent.hasClicks(vararg clickStates: ClickState): Boolean {
    return eventHasClicks(clickStates, this.isLeftClick, this.isShiftClick)
}

class UsableItem() {
    lateinit var condition: (PlayerInteractEvent) -> Boolean
    lateinit var clickEffect: (PlayerInteractEvent) -> Unit
    var itemGenerator: (() -> ItemStack)? = null
    lateinit var subEffects: List<ItemEffect>

    constructor(
        condition: (PlayerInteractEvent) -> Boolean,
        clickEffect: (PlayerInteractEvent) -> Unit,
        itemGenerator: (() -> ItemStack)? = null,
        vararg subEffects: ItemEffect
    ) : this() {
        this.condition = condition
        this.clickEffect = clickEffect
        this.itemGenerator = itemGenerator
        this.subEffects = subEffects.toList()
    }

    constructor(
        itemStack: ItemStack,
        clickEffect: (PlayerInteractEvent) -> Unit,
        vararg subEffects: ItemEffect
    ) : this() {
        condition = { event -> event.item == itemStack }
        itemGenerator = { itemStack }
        this.clickEffect = clickEffect
        this.subEffects = subEffects.toList()
    }
}

class ItemEffect() {
    lateinit var condition: (PlayerInteractEvent) -> Boolean
    lateinit var clickEffect: (PlayerInteractEvent) -> Unit
    lateinit var subEffects: List<ItemEffect>

    constructor(
        condition: (PlayerInteractEvent) -> Boolean,
        clickEffect: (PlayerInteractEvent) -> Unit,
        vararg subEffects: ItemEffect
    ) : this() {
        this.condition = condition
        this.clickEffect = clickEffect
        this.subEffects = subEffects.toList()
    }
}