package de.cypdashuhn.rooster.database.utility_tables

import com.google.gson.Gson
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.core.RoosterService
import de.cypdashuhn.rooster.database.utility_tables.ItemManager.Item.Companion.transform
import org.bukkit.Location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.KClass

class LocationManager : UtilityDatabase(Locations), RoosterService {
    object Locations : IntIdTable("RoosterLocations") {
        val key = varchar("key", 36).nullable()
        private val dataJson = text("data").nullable()
        val data = dataJson.transform(
            { data -> Gson().toJson(data) },
            { json -> Gson().fromJson(json, Any::class.java) }
        )

        val x = double("x")
        val y = double("y")
        val z = double("z")
        val worldName = varchar("world_name", 36)
        val yaw = float("yaw")
        val pitch = float("pitch")
    }

    class Location(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Location>(Locations)

        var key by Locations.key
        var data by Locations.data.transform()

        var x by Locations.x
        var y by Locations.y
        var z by Locations.z
        var worldName by Locations.worldName
        var yaw by Locations.yaw
        var pitch by Locations.pitch

        fun location(): org.bukkit.Location {
            return Location(
                Rooster.plugin.server.getWorld(worldName),
                x,
                y,
                z,
                yaw,
                pitch
            )
        }
    }

    fun insertOrGetLocation(
        location: org.bukkit.Location,
        key: String? = null,
        ignoreKeyLocations: Boolean = false,
        roundCoordinates: Boolean = false,
        ignoreAngle: Boolean = false
    ): org.bukkit.Location {
        return transaction {
            var query: Op<Boolean>
            if (roundCoordinates) {
                query = (Locations.x.castTo<Int>(IntegerColumnType()) eq location.x.toInt()) and
                        (Locations.y.castTo<Int>(IntegerColumnType()) eq location.y.toInt()) and
                        (Locations.z.castTo<Int>(IntegerColumnType()) eq location.z.toInt())
            } else {
                query = (Locations.x eq location.x) and
                        (Locations.y eq location.y) and
                        (Locations.z eq location.z)
            }

            if (!ignoreAngle) {
                query = query and
                        (Locations.yaw eq location.yaw) and
                        (Locations.pitch eq location.pitch)
            }

            if (!ignoreKeyLocations && key != null) query = query and (Locations.key eq key)
            val foundLocation = Location.find { query }.firstOrNull()

            if (foundLocation != null) return@transaction foundLocation.location()

            val dbLocation = Location.new {
                this.key = key

                this.x = location.x
                this.y = location.y
                this.z = location.z
                this.worldName = location.world.name
                this.yaw = location.yaw
                this.pitch = location.pitch
            }

            dbLocation.location()
        }
    }

    fun locationByKey(key: String): org.bukkit.Location? {
        return transaction {
            Location.find { Locations.key eq key }.firstOrNull()?.location()
        }
    }

    override fun targetClass(): KClass<out RoosterService> {
        return LocationManager::class
    }
}