package dev.cypdashuhn.build

import dev.cypdashuhn.build.commands.build.create
import dev.cypdashuhn.build.commands.build.delete
import dev.cypdashuhn.build.commands.build.edit
import dev.cypdashuhn.build.commands.build.load
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.db.FrameManager
import dev.cypdashuhn.rooster.common.RoosterServices
import dev.cypdashuhn.rooster.common.initRooster
import dev.cypdashuhn.rooster.db.db
import dev.cypdashuhn.rooster.db.utility_tables.LocationManager
import dev.cypdashuhn.rooster.db.utility_tables.PlayerManager
import dev.cypdashuhn.rooster.db.utility_tables.attributes.PlayerAttributeManager
import dev.cypdashuhn.rooster.localization.provider.LocaleProvider
import dev.cypdashuhn.rooster.localization.provider.YmlLocaleProvider
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class BuildPlugin : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
        val services = RoosterServices()
        val playerManager by services.setDelegate(PlayerManager())
        val playerAttributeManager by services.setDelegate(PlayerAttributeManager(playerManager))
        val locationManager by services.setDelegate(LocationManager())
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(false)) // Load with verbose output
    }

    override fun onEnable() {
        plugin = this

        initRooster(plugin, services) {
            services.setDelegate<LocaleProvider>(
                YmlLocaleProvider(
                    mapOf(
                        "en_US" to Locale.ENGLISH,
                        "de_DE" to Locale.GERMAN
                    ), "en_US"
                )
            )

            db(listOf(DbBuildsManager.Builds, FrameManager.Frames))
        }

        CommandAPI.onEnable()
        create()
        edit()
        load()
        delete()
    }

    override fun onDisable() {
        CommandAPI.onDisable()
    }
}