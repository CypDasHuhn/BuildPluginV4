package de.cypdashuhn.rooster.ui.items

import com.google.gson.Gson
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Slot
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KProperty1

class Dependency<T : Context> private constructor(
    private val dependOnPlayer: Boolean,
    private val dependOnSlot: Boolean,
    private val contextDependencies: List<KProperty1<T, *>>?
) {
    internal data class UpdateValue<T : Any>(val old: T, val new: T)

    internal fun shouldUpdate(
        playerValue: UpdateValue<Player>,
        slotValue: UpdateValue<Slot>,
        contextValue: UpdateValue<T>
    ): Boolean {
        return (dependOnPlayer && playerValue.old != playerValue.new) ||
                (dependOnSlot && slotValue.old != slotValue.new) ||
                contextDependencies != null && contextDependencies.any { it.get(contextValue.new) != it.get(contextValue.old) }
    }

    internal fun value(
        playerValue: UpdateValue<Player>,
        slotValue: UpdateValue<Slot>,
        contextValue: UpdateValue<T>,
        fallback: () -> ItemStack
    ): ItemStack {
        if (shouldUpdate(playerValue, slotValue, contextValue)) return fallback()
        else return fallback() // todo: actually implement
    }

    internal fun dependencyKey(
        player: Player,
        slot: Slot,
        context: T,
    ): String {
        val keys = mutableListOf<String>()
        if (dependOnPlayer) keys += "P:${player.uuid()}"
        if (dependOnSlot) keys += "S:$slot"
        if (contextDependencies == null) {
            keys += "CC:${Gson().toJson(context)}"
        } else if (contextDependencies.isNotEmpty()) {
            val gson = Gson()

            val serializedFields = contextDependencies.joinToString(",") {
                "${it.name}:${gson.toJson(it.get(context))}"
            }
            keys += "C:$serializedFields"
        }

        return keys.joinToString(",")
    }

    companion object {
        fun none(): Dependency<Context> = Dependency(false, false, emptyList())

        fun player(): Dependency<Context> = Dependency(true, false, emptyList())

        fun slot(): Dependency<Context> = Dependency(false, true, emptyList())

        fun playerAndSlot(): Dependency<Context> = Dependency(true, true, emptyList())

        fun <ContextType : Context> context(
            vararg contextDependencies: KProperty1<ContextType, *>,
            dependOnPlayer: Boolean = true,
            dependOnSlot: Boolean = true
        ): Dependency<ContextType> {
            return Dependency(dependOnPlayer, dependOnSlot, contextDependencies.toList())
        }

        fun <ContextType : Context> onlyContext(
            vararg contextDependencies: KProperty1<ContextType, *>
        ): Dependency<ContextType> {
            return Dependency(false, false, contextDependencies.toList())
        }

        fun anyContext(): Dependency<Context> = Dependency(false, false, null)

        fun all(): Dependency<Context> = Dependency(true, true, null)
    }
}