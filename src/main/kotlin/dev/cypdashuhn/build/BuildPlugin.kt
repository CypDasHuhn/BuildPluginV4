package dev.cypdashuhn.build

import com.google.common.cache.CacheBuilder
import dev.cypdashuhn.build.commands.build.create
import dev.cypdashuhn.build.commands.build.delete
import dev.cypdashuhn.build.commands.build.edit
import dev.cypdashuhn.build.commands.build.load
import dev.cypdashuhn.build.commands.test3
import dev.cypdashuhn.build.commands.test4
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.cypdashuhn.build.db.FrameManager
import dev.cypdashuhn.rooster.common.RoosterCache
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
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.util.UTF8ResourceBundleControl
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.TimeUnit


class BuildPlugin : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
        val services = RoosterServices()
        val cache = RoosterCache<String, Any>(CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES))
        val playerManager by services.setDelegate(PlayerManager())
        val playerAttributeManager by services.setDelegate(PlayerAttributeManager(playerManager))
        val locationManager by services.setDelegate(LocationManager())
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(false)) // Load with verbose output
    }

    override fun onEnable() {
        plugin = this

        val store: MiniMessageTranslationStore =
            MiniMessageTranslationStore.create(Key.key("namespace:value"))

        val bundle = ResourceBundle.getBundle("dev.cypdashuhn.build", Locale.US, UTF8ResourceBundleControl.get())
        store.registerAll(Locale.US, bundle, true)
        GlobalTranslator.translator().addSource(store)

        initRooster(plugin, services, cache) {
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
        //test()
        //test2()
        test3()
        test4()
    }

    override fun onDisable() {
        CommandAPI.onDisable()
    }
}