package de.cypdashuhn.rooster.listeners.chat

import kotlinx.coroutines.*
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object ChatManager {
    object ChatManager {
        private val playerListeners = ConcurrentHashMap<Player, ListenerData>()

        data class ListenerData(
            val onMessage: () -> Unit,
            val cancelMessage: Boolean,
            var timeoutJob: Job? = null
        )

        fun Player.onNextMessage(onMessage: () -> Unit, cancelMessage: Boolean = true) {
            playerListeners[this]?.timeoutJob?.cancel()

            playerListeners[this] = ListenerData(
                onMessage = onMessage,
                cancelMessage = cancelMessage
            )
        }

        fun Player.onNextMessage(
            onMessage: () -> Unit,
            timeOutLength: Long,
            onTimeout: () -> Unit,
            cancelMessage: Boolean = true
        ) {
            playerListeners[this]?.timeoutJob?.cancel()

            val timeoutJob = CoroutineScope(Dispatchers.Default).launch {
                delay(timeOutLength)
                playerListeners.remove(this@onNextMessage)
                onTimeout()
            }

            playerListeners[this] = ListenerData(
                onMessage = onMessage,
                cancelMessage = cancelMessage,
                timeoutJob = timeoutJob
            )
        }

        fun handlePlayerChat(player: Player, message: String): Boolean {
            val listenerData = playerListeners.remove(player) ?: return false

            listenerData.timeoutJob?.cancel()

            listenerData.onMessage()

            return listenerData.cancelMessage
        }
    }
}