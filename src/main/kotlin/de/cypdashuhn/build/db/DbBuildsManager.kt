package de.cypdashuhn.build.db

import com.sk89q.worldedit.math.Vector3
import de.cypdashuhn.rooster.database.RoosterTable
import de.cypdashuhn.rooster.database.findEntry
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object DbBuildsManager {
    @RoosterTable
    object Builds : IntIdTable() {
        val name = varchar("name", 255)
        val frameAmount = integer("frame_amount")

        val xLength = integer("x_length")
        val yLength = integer("y_length")
        val zLength = integer("z_length")

        /** time in ms the frame is left for among all frames. if null, there are multiple frame times*/
        val generalDuration = integer("generalDuration").nullable()
    }

    class Build(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Build>(Builds)

        var name by Builds.name
        var frameAmount by Builds.frameAmount

        var xLength by Builds.xLength
        var yLength by Builds.yLength
        var zLength by Builds.zLength

        var generalDuration by Builds.generalDuration
    }

    fun buildByName(name: String) = Build.findEntry(Builds.name eq name)

    fun register(name: String, dimensions: Vector3) {
        transaction {
            Build.new {
                this.name = name
                this.frameAmount = 1
                this.xLength = dimensions.x().toInt()
                this.yLength = dimensions.y().toInt()
                this.zLength = dimensions.z().toInt()
                this.generalDuration = 100
            }
        }
    }
}