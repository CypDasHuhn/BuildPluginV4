package de.cypdashuhn.rooster.database.utility_tables.attributes

import de.cypdashuhn.rooster.core.RoosterService
import de.cypdashuhn.rooster.core.RoosterServices
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import kotlin.reflect.KClass

class PlayerAttributeManager : AttributeManager<Player, EntityID<Int>>(PlayerAttributes), RoosterService {
    init {
        val playerManager = RoosterServices.getIfPresent<PlayerManager>()
        requireNotNull(playerManager) { "Player Manager must be registered. Order matters, initialize the Player Manager before this one!" }
    }

    object PlayerAttributes : Attributes("RoosterPlayerAttributes") {
        val player = reference("player", PlayerManager.Players, onDelete = ReferenceOption.CASCADE)
    }

    override fun fieldInfo(value: Player): Pair<Column<EntityID<Int>>, EntityID<Int>> =
        PlayerAttributes.player to value.dbPlayer().id

    override fun targetClass(): KClass<out RoosterService> {
        return PlayerAttributeManager::class
    }
}