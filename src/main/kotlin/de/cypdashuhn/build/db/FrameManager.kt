package de.cypdashuhn.build.db

import de.cypdashuhn.rooster.database.utility_tables.UtilityDatabase
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object FrameManager : UtilityDatabase(Frames) {
    object Frames : IntIdTable() { // Use String as the primary key type
        val build = reference("buildName", DbBuildsManager.Builds, onDelete = ReferenceOption.CASCADE)
        val frameNum = integer("frameNum")

        /** Time in ms for how long this frame will last. Null if all frames of the build have the same duration. */
        val duration = integer("duration").nullable()
        val targetFrameSchematic = integer("targetFrameSchematic").nullable()

        init {
            uniqueIndex(build, frameNum)
        }
    }

    class Frame(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, Frame>(Frames)

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

    fun newFrame(buildName: String, frameNumber: Int): Frame {
        return transaction {
            val build = DbBuildsManager.buildByName(buildName) ?: throw IllegalStateException("Build should exist")

            build.frameAmount += 1

            Frame.new {
                this.build = build
                this.frameNum = frameNumber
            }

        }
    }
}