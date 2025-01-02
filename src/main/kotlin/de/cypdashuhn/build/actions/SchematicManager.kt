package de.cypdashuhn.build.actions

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.bukkit.BukkitAdapter.asBlockVector
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.World
import de.cypdashuhn.rooster.core.Rooster
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object SchematicManager {
    fun dir(buildName: String, frame: Int): File {
        return File(Rooster.pluginFolder + "/schematics/$buildName/$frame.schem")
    }
    fun save(buildName: String, frame: Int, region: Region) {
        val file = dir(buildName, frame)
        file.parentFile.mkdirs()
        file.createNewFile()

        val clipboard = BlockArrayClipboard(region)

        val forwardExtentCopy = ForwardExtentCopy(
            region.world, region, clipboard, region.minimumPoint
        )

        Operations.complete(forwardExtentCopy)

        BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(FileOutputStream(file)).use { writer ->
            writer.write(clipboard)
        }
    }

    fun load(buildName: String, frame: Int, pos1: Location) {
        var clipboard: Clipboard
        val file = dir(buildName, frame)

        val format = ClipboardFormats.findByFile(file) ?: throw IllegalStateException("Schematic should exist")

        format.getReader(FileInputStream(file)).use { reader ->
            clipboard = reader.read()

            WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(pos1.world)).use { editSession ->
                val operation: Operation = ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(asBlockVector(pos1))
                    .build()
                Operations.complete(operation)
            }
        }
    }

    fun Player.worldEditSelection(): Region? {
        val actor = BukkitAdapter.adapt(this)
        val manager = WorldEdit.getInstance().sessionManager
        val localSession = manager.get(actor)

        val selectionWorld = localSession.selectionWorld ?: return null
        return localSession.getSelection(selectionWorld)
    }
}