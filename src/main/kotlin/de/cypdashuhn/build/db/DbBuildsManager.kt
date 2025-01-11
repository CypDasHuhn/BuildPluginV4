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
import org.joml.Vector3d

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

        /** True -> Morph, False -> Move, Null -> Morph&Move */
        val isMorph = bool("isMorph").nullable()
    }

    class Build(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Build>(Builds)

        var name by Builds.name
        var frameAmount by Builds.frameAmount

        var xLength by Builds.xLength
        var yLength by Builds.yLength
        var zLength by Builds.zLength

        var generalDuration by Builds.generalDuration

        private var isMorph by Builds.isMorph
        var morphType: BuildType
            get() = BuildType.entries.first { it.isMorph == isMorph }
            set(value) { isMorph = value.isMorph }

        val dimensions: Vector3d
            get() = Vector3d(xLength.toDouble(), yLength.toDouble(), zLength.toDouble())
    }

    enum class BuildType(val isMorph: Boolean?) {
        MORPH(true),
        MOVE(false),
        MORPH_MOVE(null)
    }

    fun buildByName(name: String) = Build.findEntry(Builds.name eq name)

    fun register(name: String, dimensions: Vector3, ) {
        transaction {
            Build.new {
                this.name = name
                this.frameAmount = 1
                this.xLength = dimensions.x().toInt()
                this.yLength = dimensions.y().toInt()
                this.zLength = dimensions.z().toInt()
                this.generalDuration = 100
                this.morphType
            }
        }
    }

    fun delete(build: Build) {
        transaction {
            build.delete()
        }
    }
}