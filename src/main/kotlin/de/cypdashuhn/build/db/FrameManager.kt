package de.cypdashuhn.build.db

import de.cypdashuhn.build.actions.BuildManager
import de.cypdashuhn.build.db.DbBuildsManager.Builds.nullable
import de.cypdashuhn.rooster.database.RoosterTable
import de.cypdashuhn.rooster.ui.context.SqlInterfaceContextProvider.InterfaceContext.Companion.referrersOn
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object FrameManager {
    @RoosterTable
    object Frames : IdTable<String>() { // Use String as the primary key type
        val build = reference("buildName", DbBuildsManager.Builds)
        val frameNum = integer("frameNum")

        /** Time in ms for how long this frame will last. Null if all frames of the build have the same duration. */
        val duration = integer("duration").nullable()
        val targetFrameSchematic = integer("targetFrameSchematic").nullable()

        override val id: Column<EntityID<String>> = varchar("id", 255).entityId()

        init {
            uniqueIndex(build, frameNum) // Ensure uniqueness of the combination
        }

        fun createId(buildName: String, frameNum: Int): String {
            return "$buildName:$frameNum"
        }

        fun parseId(id: String): FrameId {
            val parts = id.split(":")
            require(parts.size == 2) { "Invalid FrameId format" }
            return FrameId(parts[0], parts[1].toInt())
        }
    }

    data class FrameId(val build: String, val frameNum: Int) : Comparable<FrameId> {
        override fun compareTo(other: FrameId): Int {
            return compareValuesBy(this, other, { it.build }, { it.frameNum })
        }
    }

    class Frame(id: EntityID<String>) : Entity<String>(id) {
        companion object : EntityClass<String, Frame>(Frames)

        var build by DbBuildsManager.Build referencedOn Frames.build
        var frameNum by Frames.frameNum
        var duration by Frames.duration
    }

    fun getFrame(buildName: String, frameNumber: Int): Frame? {
        return transaction {
            val build = DbBuildsManager.buildByName(buildName) ?: run { return@transaction null }

            return@transaction Frame.find(Frames.build eq build.id).firstOrNull()
        }
    }
}