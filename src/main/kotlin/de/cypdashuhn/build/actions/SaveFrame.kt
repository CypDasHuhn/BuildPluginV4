package de.cypdashuhn.build.actions

import com.fastasyncworldedit.core.FaweAPI.getWorld
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter.asBlockVector
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.session.ClipboardHolder
import de.cypdashuhn.rooster.core.Rooster
import org.bukkit.Location
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object SaveFrame {
    fun save(buildName: String, pos1: Location, pos2: Location) {
        val file = File(Rooster.pluginFolder + "/schematics/${buildName}.schem")
        file.parentFile.mkdirs()
        file.createNewFile()

        val region = CuboidRegion(asBlockVector(pos1), asBlockVector(pos2))
        val clipboard = BlockArrayClipboard(region)

        BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(FileOutputStream(file)).use { writer ->
            writer.write(clipboard)
        }
    }

    fun load(buildName: String, pos1: Location) {
        var clipboard: Clipboard
        val file = File(Rooster.pluginFolder + "/schematics/${buildName}.schem")

        val format = ClipboardFormats.findByFile(file) ?: throw IllegalStateException("Schematic should exist")

        format.getReader(FileInputStream(file)).use { reader ->
            clipboard = reader.read()

            WorldEdit.getInstance().newEditSession(getWorld(pos1.world.name)).use { editSession ->
                val operation: Operation = ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(asBlockVector(pos1)) // configure here
                    .build()
                Operations.complete(operation)
            }
        }
    }
}