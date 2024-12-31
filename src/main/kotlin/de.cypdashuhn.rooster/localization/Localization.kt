package de.cypdashuhn.rooster.localization

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import de.cypdashuhn.rooster.core.Rooster.cache
import de.cypdashuhn.rooster.core.Rooster.localeProvider
import de.cypdashuhn.rooster.core.config.RoosterOptions
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object Localization {
    fun getLocalizedMessage(
        language: Language?,
        messageKey: String,
        vararg replacements: Pair<String, String?>
    ): TextComponent {
        val language = language ?: localeProvider.getGlobalLanguage()

        
        var message = cache.get("$language-$messageKey", null, {
            val resourcePath = "/locales/${language.lowercase()}.json"
            val inputStream = javaClass.getResourceAsStream(resourcePath)
                ?: return@get RoosterOptions.Localization.DEFAULT_STRING.also {
                    RoosterOptions.Warnings.LOCALIZATION_MISSING_LOCALE.warn(resourcePath)
                }

            val gson = Gson()
            val type = object : TypeToken<Map<String, String>>() {}.type
            val localization: Map<String, String> =
                gson.fromJson(InputStreamReader(inputStream, StandardCharsets.UTF_8), type)

            localization[messageKey] ?: RoosterOptions.Localization.DEFAULT_STRING.also {
                RoosterOptions.Warnings.LOCALIZATION_MISSING_LOCALE.warn(messageKey to language)
            }
        }, 60, TimeUnit.MINUTES)

        for ((key, value) in replacements) {
            message = message.replace("\${$key}", value ?: "")
        }

        return MiniMessage.miniMessage().deserialize(message) as TextComponent
    }
}

fun t(messageKey: String, language: Language?, vararg replacements: Pair<String, String?>): TextComponent {
    return Localization.getLocalizedMessage(language, messageKey, *replacements)
}

fun t(messageKey: String, player: Player, vararg replacements: Pair<String, String?>): TextComponent {
    return Localization.getLocalizedMessage(localeProvider.getLanguage(player), messageKey, *replacements)
}

fun CommandSender.tSendWLanguage(messageKey: String, language: Language?, vararg replacements: Pair<String, String>) {
    this.sendMessage(t(messageKey, language, *replacements))
}

fun CommandSender.tSend(messageKey: String, vararg replacements: Pair<String, String?>) {
    this.sendMessage(t(messageKey, this.language(), *replacements))
}

fun CommandSender.language(): Language {
    return if (this is Player) localeProvider.getLanguage(this)
    else localeProvider.getGlobalLanguage()
}

class Locale(var language: Language?) {
    private val actualLocale: Language by lazy { language ?: localeProvider.getGlobalLanguage() }
    fun t(messageKey: String, vararg replacements: Pair<String, String?>): TextComponent {
        return Localization.getLocalizedMessage(actualLocale, messageKey, *replacements)
    }

    fun tSend(sender: CommandSender, messageKey: String, vararg replacements: Pair<String, String?>) {
        sender.sendMessage(t(messageKey, *replacements))
    }
}

fun CommandSender.locale(): Locale {
    return Locale(this.language())
}

fun t(messageKey: String, vararg replacements: Pair<String, String>): String {
    return "<t>$messageKey<rp>${replacements.joinToString("<next>") { "<key>${it.first}<value>${it.second}" }}"
}

fun transformMessage(message: String, language: Language?): String {
    return when {
        message.startsWith("!<t>") -> message.drop(1)
        message.startsWith("<t>") -> {
            val (key, replacements) = decryptTranslatableMessage(message)
            t(key, language, *replacements).content()
        }

        else -> message
    }
}

fun decryptTranslatableMessage(message: String): Pair<String, Array<Pair<String, String>>> {
    val (key, rest) = message.split("<rp>", limit = 2)
    val replacements = if (rest.isNotEmpty()) rest.split("<next>").map {
        val (replacementKey, replacementValue) = it.split("<value>")
        replacementKey.drop("<key>".length) to replacementValue
    } else listOf()
    return key to replacements.toTypedArray()
}

fun translateLanguage(message: String, language: Language?, vararg replacements: Pair<String, String>): String {
    return t(message, language, *replacements).content()
}

fun main() {
    val language = "en"

    val r1 = t("test")
    val r2 = t("test", "a" to "b")
    val r5 = t("test", "a" to "b", "c" to "d")

    val t1 = transformMessage(r1, language)
    val t2 = transformMessage(r2, language)
    val t3 = transformMessage("test", language)
    val t4 = transformMessage("!<t>test", language)
    val t5 = transformMessage(r5, language)

    val s = ""
}