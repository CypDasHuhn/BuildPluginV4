package de.cypdashuhn.rooster.localization.provider

import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import org.bukkit.entity.Player
import java.util.*

typealias Language = String

abstract class LocaleProvider(open var locales: Map<Language, Locale>, open var defaultLocale: Language) {

    protected abstract fun playerLanguage(player: Player): Language?

    abstract fun changeLanguage(player: Player, language: Language)

    abstract fun getGlobalLanguage(): Language

    abstract fun changeGlobalLanguage(language: Language)

    fun getLanguage(player: Player): Language {
        return playerLanguage(player) ?: getGlobalLanguage()
    }

    fun init() {
        val registry: TranslationRegistry = TranslationRegistry.create(Key.key("myplugin"))

        locales.forEach { (language, locale) ->
            val resourceBundle = ResourceBundle.getBundle(language)
            registry.registerAll(locale, resourceBundle, true)
        }

        GlobalTranslator.translator().addSource(registry)
    }
}