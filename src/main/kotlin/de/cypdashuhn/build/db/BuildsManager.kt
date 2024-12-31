package de.cypdashuhn.build.db

import de.cypdashuhn.rooster.database.RoosterTable
import de.cypdashuhn.rooster.database.findEntry
import org.bukkit.Location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object BuildsManager {
    @RoosterTable
    object Builds : IntIdTable() {
        val name = varchar("name", 255)
        val frameAmount = integer("frame_amount")

        val xLength = integer("x_length")
        val yLength = integer("y_length")
        val zLength = integer("z_length")
    }

    class Build(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Build>(Builds)

        var name by Builds.name
        var frameAmount by Builds.frameAmount

        var xLength by Builds.xLength
        var yLength by Builds.yLength
        var zLength by Builds.zLength
    }

    fun buildByName(name: String) = Build.findEntry(Builds.name eq name)

    fun register(name: String, pos1: Location, pos2: Location) {
        transaction {
            Build.new {
                this.name = name
                this.frameAmount = 1
                this.xLength = (pos2.x - pos1.x).toInt()
                this.yLength = (pos2.y - pos1.y).toInt()
                this.zLength = (pos2.z - pos1.z).toInt()
            }
        }
    }
}