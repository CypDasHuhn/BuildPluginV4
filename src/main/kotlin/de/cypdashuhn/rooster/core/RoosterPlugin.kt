package de.cypdashuhn.rooster.core

import org.bukkit.plugin.java.JavaPlugin

/**
 * The usual way of registering a Rooster Plugin.
 *
 * This class provides key hooks for integrating with Rooster's
 * initialization lifecycle.
 *
 * If you need to create your plugin without extending RoosterPlugin, read
 * here about the alternative:
 *
 * TODO: Add Doc Link
 */
abstract class RoosterPlugin(private val pluginName: String, scanTarget: String? = null) : JavaPlugin(), RoosterShell {
    final override fun onEnable() {
        val start = System.currentTimeMillis()

        initializeRooster(this, pluginName)

        val duration = System.currentTimeMillis() - start
        Rooster.roosterLogger.info("Plugin $pluginName took ${duration}ms to initialize")
    }

    final override fun initializeRooster(plugin: JavaPlugin, pluginName: String) =
        super.initializeRooster(plugin, pluginName)
}
