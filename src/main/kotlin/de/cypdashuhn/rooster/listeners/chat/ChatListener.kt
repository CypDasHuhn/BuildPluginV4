package de.cypdashuhn.rooster.listeners.chat

import de.cypdashuhn.rooster.listeners.RoosterListener
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import java.net.http.WebSocket.Listener

@RoosterListener
object ChatListener : Listener {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        val message = event.message()
    }
}