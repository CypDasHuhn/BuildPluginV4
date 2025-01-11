package de.cypdashuhn.build.actions

import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.build.db.FrameManager
import de.cypdashuhn.rooster.core.Rooster.plugin
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster.region.Region
import de.cypdashuhn.rooster.region.compareVectors
import de.cypdashuhn.rooster_worldedit.adapter.toWorldEditRegion
import de.cypdashuhn.rooster_worldedit.adapter.worldEditSelection
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

object BuildManager {
    fun create(player: Player, buildName: String) {
        val region = player.worldEditSelection() ?: run {
            player.tSend("build_missing_selection")
            return
        }

        DbBuildsManager.register(buildName, region.dimensions.toVector3())
        FrameManager.newFrame(buildName, 1)
        SchematicManager.save(buildName, 1, region)
    }

    fun load(player: Player, build: DbBuildsManager.Build, frame: Int, pos1: Location, pos2: Location?) {
        SchematicManager.load(build.name, frame, target(player, pos1, pos2))
    }

    fun loadAll(player: Player, build: DbBuildsManager.Build, pos1: Location, pos2: Location?) {
        val frames = build.frameAmount
        val generalDuration = build.generalDuration

        if (pos2 != null) {
            val region = Region(pos1, pos2)

            val canProceed = compareVectors(player, build.dimensions, region.dimensions)
            if (!canProceed) return
        }

        val corner = if (pos2 == null) pos1 else Region(pos1, pos2).min

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            (1..frames).forEach { frameNumber ->
                val duration = generalDuration ?: run {
                    val frame = FrameManager.getFrame(build.name, frameNumber)
                        ?: throw IllegalStateException("Frame should exist")

                    frame.duration ?: throw IllegalStateException("Frame duration should exist")
                }

                Bukkit.getScheduler().runTask(plugin, Runnable {
                    load(player, build, frameNumber, corner, null)
                })

                Thread.sleep(duration.toLong())
            }
        })
    }

    private fun target(player: Player, pos1: Location, pos2: Location?): Location {
        return if (pos2 == null) pos1 else {
            Region(pos1, pos2).min
        }
    }

    fun save(player: Player, build: DbBuildsManager.Build, frame: Int, pos1: Location, pos2: Location?) {
        val frames = build.frameAmount

        assert(frame in 1..frames + 1) { "Frame number must be between 1 and ${frames + 1}" }

        if (pos2 != null) {
            val region = Region(pos1, pos2)

            val canProceed = compareVectors(player, build.dimensions, region.dimensions)
            if (!canProceed) return
        }
        if (frame == frames + 1) {
            FrameManager.newFrame(build.name, frame)
        }

        val corner = if (pos2 == null) pos1 else Region(pos1, pos2).min
        val secondCorner =
            pos2 ?: pos1.add(build.xLength.toDouble(), build.yLength.toDouble(), build.zLength.toDouble())

        SchematicManager.save(build.name, frame, Region(corner, secondCorner).toWorldEditRegion())
    }

    fun delete(build: DbBuildsManager.Build) {
        DbBuildsManager.delete(build)
    }
}