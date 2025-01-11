package de.cypdashuhn.rooster.listeners.chat

import de.cypdashuhn.rooster.localization.language
import de.cypdashuhn.rooster.localization.minimessage
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object ChatManager {
    private val playerListeners = ConcurrentHashMap<Player, ListenerData>()

    fun callListener(event: AsyncChatEvent) {
        val player = event.player
        val data = playerListeners[player] ?: return
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        if (data.cancelMessage) event.isCancelled = true
        data.onMessage(event, message)
        playerListeners.remove(player)
    }

    data class ListenerData(
        val onMessage: (AsyncChatEvent, String) -> Unit,
        val cancelMessage: Boolean,
        var timeoutJob: Job? = null
    )

    fun Player.onNextMessage(onMessage: (AsyncChatEvent, String) -> Unit, cancelMessage: Boolean = true) {
        playerListeners[this]?.timeoutJob?.cancel()

        playerListeners[this] = ListenerData(
            onMessage = onMessage,
            cancelMessage = cancelMessage
        )
    }

    fun Player.onNextMessage(
        onMessage: (AsyncChatEvent, String) -> Unit,
        /** Time in seconds */
        timeOutLength: Int,
        onTimeout: () -> Unit,
        cancelMessage: Boolean = true
    ) {
        playerListeners[this]?.timeoutJob?.cancel()

        val timeoutJob = CoroutineScope(Dispatchers.Default).launch {
            delay(timeOutLength * 1000L)
            playerListeners.remove(this@onNextMessage)
            onTimeout()
        }

        playerListeners[this] = ListenerData(
            onMessage = onMessage,
            cancelMessage = cancelMessage,
            timeoutJob = timeoutJob
        )
    }

    fun Player.clickConfirmation(
        onConfirm: (Player) -> Unit,
        onCancel: (Player) -> Unit,
        infoMessage: String = "rooster.chat.info",
        confirmText: String = "rooster.chat.confirm",
        cancelText: String = "rooster.chat.cancel",
        /** Timeout Duration in Seconds */
        timeOutDuration: Int = 10,
        alreadyTimeoutText: String = "rooster.chat.already_timeout_error",
    ) {
        val timeoutJob = CoroutineScope(Dispatchers.Default).launch {
            delay(timeOutDuration * 1000L)

            onCancel(this@clickConfirmation)
        }

        this.tSend(infoMessage)
        val confirm = minimessage(t(confirmText, this.language())).asComponent().clickEvent(ClickEvent.callback {
            if (!timeoutJob.isCancelled) onConfirm(this@clickConfirmation)
            else this@clickConfirmation.tSend(alreadyTimeoutText)
        })

        val cancel = minimessage(t(cancelText, this.language())).asComponent().clickEvent(ClickEvent.callback {
            if (!timeoutJob.isCancelled) onCancel(this@clickConfirmation)
            else this@clickConfirmation.tSend(alreadyTimeoutText)
        })
        val both = Component.text().append(confirm).append(minimessage("<white> | ")).append(cancel).build()
        this.sendMessage(both)
    }

    fun Player.chatConfirmation(
        onConfirm: (Player) -> Unit,
        onCancel: (Player) -> Unit,
        infoMessage: String = "rooster.chat.info",
        confirmMessage: String = "rooster.chat.confirm",
        /** Timeout Duration in Seconds */
        timeOutDuration: Int = 10
    ) {
        this.tSend(infoMessage)
        this.onNextMessage(
            onMessage = { event, message ->
                val player = event.player
                if (t(confirmMessage, player.language()).content().equals(message, ignoreCase = true)) onConfirm(player)
                else onCancel(player)
            },
            timeOutLength = timeOutDuration,
            onTimeout = {
                onCancel(this)
            }
        )
    }
}