package de.cypdashuhn.rooster.listeners.chat

import de.cypdashuhn.rooster.listeners.RoosterListener
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@RoosterListener
object ChatListener : Listener {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        ChatManager.callListener(event)
    }
}