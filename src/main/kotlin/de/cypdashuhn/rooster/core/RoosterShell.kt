package de.cypdashuhn.rooster.core

import org.bukkit.plugin.java.JavaPlugin

interface RoosterShell {
    /**
     * Called before Rooster initializes. This is where you modify Rooster's
     * core variables.
     *
     * This hook gives you early access to adjust settings before Rooster runs
     * its setup process. Read more about it here: TODO: Add Doc Link
     */
    fun beforeInitialize() {}

    /**
     * Called after Rooster has fully initialized. Ideal for database access or
     * setup logic.
     *
     * You can safely assume all of Rooster's services are available when this
     * is called. Read more about it here: TODO: Add Doc Link
     */
    fun onInitialize() {}


    fun initializeRooster(plugin: JavaPlugin, pluginName: String) {
        beforeInitialize()
        Rooster.initialize(
            plugin = plugin,
            pluginName = pluginName,
        )
        onInitialize()
    }
}