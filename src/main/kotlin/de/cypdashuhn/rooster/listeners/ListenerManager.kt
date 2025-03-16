package de.cypdashuhn.rooster.listeners

import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.core.hasRoosterIgnore
import org.bukkit.event.Listener

open class RoosterListener : Listener {
    init {
        if (!hasRoosterIgnore(this)) Rooster.registeredListeners += this
    }
}