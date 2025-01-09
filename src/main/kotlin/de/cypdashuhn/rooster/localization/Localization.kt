package de.cypdashuhn.rooster.localization

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import de.cypdashuhn.rooster.core.Rooster.cache
import de.cypdashuhn.rooster.core.Rooster.localeProvider
import de.cypdashuhn.rooster.core.config.RoosterOptions
import de.cypdashuhn.rooster.localization.provider.Language
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object Localization {
    data class TreeNode(val value: String? = null, val children: Map<String, TreeNode> = emptyMap()) {
        constructor(value: String) : this(value = value, children = emptyMap())

        constructor(children: Map<String, TreeNode>) : this(value = null, children = children)
    }

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

            val localization = parseLocalization(inputStream)

            val message = getValueFromNestedMap(localization, messageKey)
            if (message != null) return@get message

            val roosterResourcePath = "/roosterLocales/${language.lowercase()}.json"
            val roosterInputStream = javaClass.getResourceAsStream(roosterResourcePath)
                ?: throw IllegalStateException("Rooster should've crashed")

            val roosterLocalization = parseLocalization(roosterInputStream)
            val roosterMessage = getValueFromNestedMap(roosterLocalization, messageKey)

            if (roosterMessage != null) return@get roosterMessage

            RoosterOptions.Warnings.LOCALIZATION_MISSING_LOCALE.warn(messageKey to language)
            return@get RoosterOptions.Localization.DEFAULT_STRING
        }, 60, TimeUnit.MINUTES)

        for ((key, value) in replacements) {
            message = message.replace("\${$key}", value ?: "")
        }

        return MiniMessage.miniMessage().deserialize(message) as TextComponent
    }

    fun parseLocalization(inputStream: InputStream): Map<String, TreeNode> {
        val gson = Gson()

        val type = object : TypeToken<Map<String, Any>>() {}.type
        val rawLocalization: Map<String, Any> =
            gson.fromJson(InputStreamReader(inputStream, StandardCharsets.UTF_8), type)

        return rawLocalization.mapValues { (_, value) -> convertToTreeNode(value) }
    }

    fun convertToTreeNode(value: Any): TreeNode {
        return when (value) {
            is String -> TreeNode(value)
            is Map<*, *> -> {
                val children =
                    value.mapValues { (_, childValue) -> convertToTreeNode(childValue!!) } as Map<String, TreeNode>
                TreeNode(children)
            }

            else -> throw IllegalArgumentException("Unsupported value type: $value")
        }
    }

    fun getValueFromNestedMap(map: Map<String, TreeNode>, key: String): String? {
        val keys = key.split(".")
        var currentMap = map

        for (i in 0 until keys.size - 1) {
            val currentNode = currentMap[keys[i]]
            if (currentNode == null || currentNode.children.isEmpty()) {
                return null
            }
            currentMap = currentNode.children
        }

        return currentMap[keys.last()]?.value
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
    return key.drop(3) to replacements.toTypedArray()
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