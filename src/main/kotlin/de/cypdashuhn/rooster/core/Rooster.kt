package de.cypdashuhn.rooster.core

import com.google.common.cache.CacheBuilder
import de.cypdashuhn.rooster.RoosterCache
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.parsing.Command
import de.cypdashuhn.rooster.commands.parsing.Completer
import de.cypdashuhn.rooster.database.initDatabase
import de.cypdashuhn.rooster.demo.DemoManager
import de.cypdashuhn.rooster.localization.provider.LocaleProvider
import de.cypdashuhn.rooster.localization.provider.SqlLocaleProvider
import de.cypdashuhn.rooster.ui.context.InterfaceContextProvider
import de.cypdashuhn.rooster.ui.context.SqlInterfaceContextProvider
import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Table
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object Rooster {
    lateinit var plugin: JavaPlugin
    internal val roosterLogger: Logger = Logger.getLogger("Rooster")
    val logger: Logger
        get() {
            if (!::plugin.isInitialized) {
                throw IllegalStateException("Plugin is not initialized. Do not use the Logger before Rooster is initialized.")
            }
            return plugin.logger
        }

    fun runTask(task: () -> Unit) {
        if (!::plugin.isInitialized) {
            throw IllegalStateException("Plugin is not initialized. Do not run tasks here before Rooster is initialized.")
        }
        Bukkit.getScheduler().runTask(plugin, Runnable { task() })
    }

    lateinit var pluginName: String

    var databasePath: String? = null
    val pluginFolder: String by lazy { plugin.dataFolder.absolutePath }
    val roosterFolder: String by lazy { plugin.dataFolder.parentFile.resolve("Rooster").absolutePath }

    val registeredCommands: MutableList<RoosterCommand> = mutableListOf()
    val registeredInterfaces: MutableList<RoosterInterface<*>> = mutableListOf()
    val registeredTables: MutableList<Table> = mutableListOf()
    val registeredDemoTables: MutableList<Table> = mutableListOf()
    val registeredDemoManager: MutableList<DemoManager> = mutableListOf()
    val registeredListeners: MutableList<Listener> = mutableListOf()
    val registeredFunctions: MutableMap<String, Method> = mutableMapOf()

    val cache = RoosterCache<String, Any>(
        CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
    )

    var dynamicTables = mutableListOf<Table>()

    internal val localeProvider by RoosterServices.delegate<LocaleProvider>()
    internal val interfaceContextProvider by RoosterServices.delegate<InterfaceContextProvider>()

    fun initServices() {
        RoosterServices.set<LocaleProvider>(SqlLocaleProvider(mapOf("en_US" to Locale.ENGLISH), "en_US"))
        RoosterServices.set<InterfaceContextProvider>(SqlInterfaceContextProvider())
    }

    fun initialize(
        plugin: JavaPlugin,
        pluginName: String,
    ) {
        Rooster.plugin = plugin
        this.pluginName = pluginName
        if (databasePath == null) databasePath = plugin.dataFolder.resolve("database.db").absolutePath

        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }

        val tables = dynamicTables + registeredTables
        initDatabase(tables, databasePath!!)

        // listeners
        val pluginManager = Bukkit.getPluginManager()
        for (listener in registeredListeners) {
            pluginManager.registerEvents(listener, plugin)
        }

        // commands
        registeredCommands.flatMap { it.labels }.forEach { label ->
            plugin.getCommand(label)?.let {
                it.setExecutor(Command)
                it.tabCompleter = Completer
            }
        }
    }
}
