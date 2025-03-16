package de.cypdashuhn.rooster.database.utility_tables

import de.cypdashuhn.rooster.core.RoosterService
import de.cypdashuhn.rooster.core.RoosterServices
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Players.uuid
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.KClass

/**
 * Not Completely Necessary. Use BukkitAPI instead. This manager is if your
 * call frequency exceeds API Limitations, or whatever else you'd like to
 * do.
 */
class PlayerManager private constructor() : UtilityDatabase(Players), RoosterService {
    internal var beforePlayerJoin: (PlayerJoinEvent) -> Unit = {}
    internal var onPlayerJoin: (PlayerJoinEvent) -> Unit = {}

    constructor(
        beforePlayerJoin: (PlayerJoinEvent) -> Unit = {},
        onPlayerJoin: (PlayerJoinEvent) -> Unit = {}
    ) : this() {
        this.beforePlayerJoin = beforePlayerJoin
        this.onPlayerJoin = onPlayerJoin
    }

    fun beforePlayerJoin(event: (PlayerJoinEvent) -> Unit) {
        beforePlayerJoin = event
    }

    fun onPlayerJoin(event: (PlayerJoinEvent) -> Unit) {
        onPlayerJoin = event
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
            val playerManager = RoosterServices.getIfPresent<PlayerManager>()
            requireNotNull(playerManager) { "Player Manager must be registered" }
            return playerManager.playerByUUID(this.uuid())!!
        }
    }

    override fun targetClass(): KClass<out RoosterService> {
        return PlayerManager::class
    }
}