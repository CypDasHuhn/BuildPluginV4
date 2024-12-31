package de.cypdashuhn.rooster.ui.context

import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import org.bukkit.entity.Player

abstract class InterfaceContextProvider {
    /**
     * (Upsert) Saves a Context-Instance, with the keys player and interface
     * (recommended: PlayerUUID, InterfaceName)
     */
    abstract fun <T : Context> updateContext(player: Player, interfaceInstance: Interface<T>, context: T)

    /**
     * Reads the Context-Instance if it exists, with the keys player and
     * interface (recommended: PlayerUUID, InterfaceName)
     */
    abstract fun <T : Context> getContext(player: Player, interfaceInstance: Interface<T>): T?
}