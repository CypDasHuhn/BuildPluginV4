package de.cypdashuhn.rooster.database.utility_tables

import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Players.uuid
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Not Completely Necessary. Use BukkitAPI instead. This manager is if your
 * call frequency exceeds API Limitations, or whatever else you'd like to
 * do.
 */
class PlayerManager : UtilityDatabase(Players) {
    init {
        this.also { Rooster.playerManager = it }
    }

    object Players : IntIdTable("RoosterPlayers") {
        val uuid = varchar("uuid", 36)
        val name = varchar("name", 16)
        val lastLogin = long("last_login")
    }

    class DbPlayer(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<DbPlayer>(Players)

        var uuid by Players.uuid
        var name by Players.name
        var lastLogin by Players.lastLogin

        val online
            get() = bukkitPlayer != null

        val bukkitPlayer: Player?
            get() = Bukkit.getWorlds().flatMap { it.players }.first { it.uuid() == uuid }
    }


    fun playerLogin(player: Player) {
        return transaction {
            DbPlayer.find { uuid eq player.uuid() }.firstOrNull()?.delete()

            Players.insert {
                it[uuid] = player.uuid()
                it[name] = player.name
                it[lastLogin] = player.lastLogin
            }
        }
    }

    fun playerByUUID(uuid: String): DbPlayer? {
        return transaction { DbPlayer.find { Players.uuid eq uuid }.firstOrNull() }
    }

    fun playerByName(name: String): DbPlayer? {
        return transaction { DbPlayer.find { Players.name eq name }.firstOrNull() }
    }

    fun players(): List<DbPlayer> {
        return transaction { DbPlayer.all().toList() }
    }

    companion object {
        fun Player.dbPlayer(): DbPlayer {
            requireNotNull(Rooster.playerManager) { "Player Manager must be registered" }
            return Rooster.playerManager!!.playerByUUID(this.uuid())!!
        }
    }
}