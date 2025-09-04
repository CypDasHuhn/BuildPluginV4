package dev.cypdashuhn.build.actions

import dev.cypdashuhn.build.BuildPlugin
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.db.FrameManager
import dev.cypdashuhn.build.worldedit.toWorldEditRegion
import dev.cypdashuhn.rooster.common.region.Region
import dev.cypdashuhn.rooster.common.region.compareVectors
import dev.cypdashuhn.rooster.common.util.tSend
import dev.cypdashuhn.rooster.common.util.wrap
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

object BuildManager {
    const val buildPluginLastCacheKey = "build_last_frame"
    fun updateLastFrame(player: Player, build: DbBuildsManager.Build, frame: Int) {
        BuildPlugin.cache.put(buildPluginLastCacheKey + build.id, player, frame)
    }

    fun getLastFrame(player: Player, build: DbBuildsManager.Build): Int? {
        return BuildPlugin.cache.getIfPresent(buildPluginLastCacheKey + build.id, player) as Int?
    }

    fun create(player: Player, buildName: String, region: Region) =
        wrap(player, "build.create.error", "build" to buildName) {
            DbBuildsManager.register(buildName, region.dimensions)
            FrameManager.newFrame(buildName, 1)
            SchematicManager.save(buildName, 1, region.toWorldEditRegion())
            updateLastFrame(player, DbBuildsManager.buildByName(buildName)!!, 1)
            player.tSend("build.create.success", "build" to buildName)
        }

    fun load(player: Player, build: DbBuildsManager.Build, frame: Int, region: Region) =
        wrap(player, "build.load.single.error", "build" to build.name, "frame" to frame.toString()) {
            load(player, build, frame, region.edge1, region.edge2)
        }

    fun load(player: Player, build: DbBuildsManager.Build, frame: Int, pos1: Location, pos2: Location?) {
        SchematicManager.load(build.name, frame, target(pos1, pos2))
    }

    fun loadAll(player: Player, build: DbBuildsManager.Build, region: Region) =
        loadAll(player, build, region.edge1, region.edge2)

    fun loadAll(player: Player, build: DbBuildsManager.Build, pos1: Location, pos2: Location?) =
        wrap(player, "build.load.all.error", "build" to build.name) {
            val frames = build.frameAmount
            val generalDuration = build.generalDuration

            if (pos2 != null) {
                val region = Region(pos1, pos2)

                val canProceed = compareVectors(player, build.dimensions, region.dimensions)
                if (!canProceed) return@wrap
            }

            val corner = if (pos2 == null) pos1 else Region(pos1, pos2).min

            Bukkit.getScheduler().runTaskAsynchronously(BuildPlugin.plugin, Runnable {
                (1..frames).forEach { frameNumber ->
                    val duration = generalDuration ?: run {
                        val frame = FrameManager.getFrame(build.name, frameNumber)
                            ?: throw IllegalStateException("Frame should exist")

                        frame.duration ?: throw IllegalStateException("Frame duration should exist")
                    }

                    Bukkit.getScheduler().runTask(BuildPlugin.plugin, Runnable {
                        load(player, build, frameNumber, corner, null)
                    })

                    Thread.sleep(duration.toLong())
                }
            })
        }

    fun save(player: Player, build: DbBuildsManager.Build, frame: Int, region: Region) =
        save(player, build, frame, region.edge1, region.edge2)

    fun save(player: Player, build: DbBuildsManager.Build, frame: Int, pos1: Location, pos2: Location?) =
        wrap(player, "build.edit.success", "build" to build.name, "frame" to frame.toString()) {
            transaction { build.refresh() }
            val frames = build.frameAmount
            assert(frame in 1..frames + 1) { "Frame number must be between 1 and ${frames + 1}" }

            if (pos2 != null) {
                val region = Region(pos1, pos2)

                val canProceed = compareVectors(player, build.dimensions, region.dimensions)
                if (!canProceed) return@wrap
            }
            if (frame == frames + 1) {
                FrameManager.newFrame(build.name, frame)
            }

            val corner = if (pos2 == null) pos1 else Region(pos1, pos2).min
            val secondCorner =
                pos2 ?: pos1.add(build.xLength.toDouble(), build.yLength.toDouble(), build.zLength.toDouble())

            SchematicManager.save(build.name, frame, Region(corner, secondCorner).toWorldEditRegion())
            updateLastFrame(player, build, frame)
            player.tSend("build.edit.success", "build" to build.name, "frame" to frame.toString())
        }

    fun delete(player: Player, build: DbBuildsManager.Build) =
        wrap(player, "build.delete.error", "build" to build.name) {
            DbBuildsManager.delete(build)
            player.tSend("build.edit.success", "build" to build.name)
        }

    private fun target(pos1: Location, pos2: Location?): Location {
        return if (pos2 == null) pos1 else {
            Region(pos1, pos2).min
        }
    }
}