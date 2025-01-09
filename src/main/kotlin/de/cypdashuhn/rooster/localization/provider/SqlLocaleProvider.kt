package de.cypdashuhn.rooster.localization.provider

import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.database.findEntry
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class SqlLocaleProvider(override var locales: Map<Language, Locale>, override var defaultLocale: String) :
    LocaleProvider(locales, defaultLocale) {
    init {
        Rooster.dynamicTables += PlayerLanguages
    }

    object PlayerLanguages : IntIdTable("RoosterLocalization") {
        val playerUUID = varchar("player_uuid", 50).nullable()
        val language = varchar("language", 50)
    }

    class PlayerLanguage(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<PlayerLanguage>(PlayerLanguages)

        var playerUUID by PlayerLanguages.playerUUID
        var language by PlayerLanguages.language
    }

    override fun playerLanguage(player: Player): Language? {
        return PlayerLanguage.findEntry(PlayerLanguages.playerUUID eq player.uuid())?.language
    }

    override fun changeLanguage(player: Player, language: Language) {
        transaction {
            val existingEntry = PlayerLanguage.find { PlayerLanguages.playerUUID eq player.uuid() }.firstOrNull()
            if (existingEntry != null) {
                existingEntry.language = language
            } else {
                PlayerLanguage.new {
                    playerUUID = player.uuid()
                    this.language = language
                }
            }
        }
    }

    private val globalLanguageKey = "global_language"
    override fun getGlobalLanguage(): Language {
        return transaction {
            PlayerLanguage.findEntry(PlayerLanguages.playerUUID eq null)?.language ?: defaultLocale.also {
                changeGlobalLanguage(defaultLocale)
            }
        }
    }

    override fun changeGlobalLanguage(language: Language) {
        transaction {
            val existingEntry = PlayerLanguage.find { PlayerLanguages.playerUUID eq null }.firstOrNull()
            if (existingEntry != null) {
                existingEntry.language = language
            } else {
                PlayerLanguage.new {
                    playerUUID = null
                    this.language = language
                }
            }
        }
    }
}