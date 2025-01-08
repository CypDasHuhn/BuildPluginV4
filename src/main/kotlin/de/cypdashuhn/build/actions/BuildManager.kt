package de.cypdashuhn.build.actions

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import de.cypdashuhn.build.actions.SchematicManager.worldEditSelection
import de.cypdashuhn.build.db.DbBuildsManager
import de.cypdashuhn.build.db.FrameManager
import de.cypdashuhn.rooster.core.Rooster.plugin
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

object BuildManager {
    fun create(player: Player, buildName: String) {
        val region = player.worldEditSelection() ?: run {
            player.tSend("build_missing_selection")
            return
        }

        DbBuildsManager.register(buildName, region.dimensions.toVector3())
        SchematicManager.save(buildName, 1, region)
    }

    fun selectionCorner(player: Player): Location? {
        val region = player.worldEditSelection() ?: run {
            player.tSend("build_missing_selection")
            return null
        }

        return region.minimumPoint.toLocation(player.world)
    }

    fun Player.selection(): Region? {
        val region = this.worldEditSelection() ?: run {
            this.tSend("build_missing_selection")
            return null
        }

        return region
    }

    fun load(player: Player, buildName: String, frame: Int, pos1: Location, pos2: Location?) {
        SchematicManager.load(buildName, frame , target(player, pos1, pos2))
    }

    fun loadAll(player: Player, buildName: String, pos1: Location, pos2: Location?) {
        val build = DbBuildsManager.buildByName(buildName) ?: throw IllegalStateException("Build should exist")
        val frames = build.frameAmount
        val generalDuration = build.generalDuration

        if (pos2 != null) {
            val region = region(pos1, pos2)
            val dimensions = region.dimensions

            if (build.xLength != dimensions.x() || build.yLength != dimensions.y() || build.zLength != dimensions.z()) {
                player.tSend("build_dimensions_mismatch")
                return
            }
        }

        val corner = if (pos2 == null) pos1 else region(pos1, pos2).minimumPoint.toLocation(pos1.world)

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            (1..frames).forEach { frameNumber ->
                val duration = generalDuration ?: run {
                    val frame = FrameManager.getFrame(buildName, frameNumber)
                        ?: throw IllegalStateException("Frame should exist")

                    frame.duration ?: throw IllegalStateException("Frame duration should exist")
                }

                Bukkit.getScheduler().runTask(plugin, Runnable {
                    load(player, buildName, frameNumber, corner, null)
                })

                Thread.sleep(duration.toLong())
            }
        })
    }

    private fun target(player: Player, pos1: Location, pos2: Location?): Location {
        return if (pos2 == null) pos1 else {
            region(pos1, pos2).minimumPoint.toLocation(pos1.world)
        }
    }

    fun BlockVector3.toLocation(world: World): Location {
        return Location(world, this.x().toDouble(), this.y().toDouble(), this.z().toDouble())
    }
}

fun region(pos1: Location, pos2: Location): CuboidRegion {
    return CuboidRegion(
        BukkitAdapter.adapt(pos1.world),
        BukkitAdapter.asBlockVector(pos1),
        BukkitAdapter.asBlockVector(pos2)
    )
}