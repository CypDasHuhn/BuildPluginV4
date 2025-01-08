package de.cypdashuhn.rooster.simulator

import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.core.RoosterShell
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class RoosterSimulator(val pluginName: String = "demo") : RoosterShell {
    fun startTerminal() {
        Simulator.initializeSimulator(this)

        Simulator.startTerminal()
    }

    fun simulate(preserveFolder: Boolean = false, actionBlock: (Player) -> Unit) {
        val player = Simulator.initializeSimulator(this)

        actionBlock(player)
        if (!preserveFolder) Simulator.deleteMockDirectory()
    }

    final override fun initializeRooster(plugin: JavaPlugin, pluginName: String) {
        super.initializeRooster(plugin, pluginName)
        val directoryPath = Rooster.plugin.dataFolder.absolutePath
        val clickablePath =
            "file:///$directoryPath".replace("\\", "/") // Ensure the format is proper for clickable URLs
        println("Current Mock Directory: $clickablePath")
    }

}