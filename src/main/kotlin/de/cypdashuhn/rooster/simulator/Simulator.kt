package de.cypdashuhn.rooster.simulator

import be.seeseemelk.mockbukkit.MockBukkit
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.core.RoosterServices
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.simulator.commands.CommandSimulatorHandler
import de.cypdashuhn.rooster.simulator.interfaces.InterfaceSimulatorHandler
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.Inventory

object Simulator {
    var currentInventory: Inventory? = null
    var currentContext: Context? = null
    var currentInterface: RoosterInterface<Context>? = null
    var player: Player? = null

    fun initializeSimulator(roosterSimulator: RoosterSimulator): Player {
        isSimulating = true

        val server = MockBukkit.mock()

        val plugin = MockBukkit.createMockPlugin()


        roosterSimulator.beforeInitialize()
        Rooster.dynamicTables.addAll(Rooster.registeredDemoTables)

        roosterSimulator.initializeRooster(plugin, roosterSimulator.pluginName)

        roosterSimulator.onInitialize()

        val player = server.addPlayer()

        val event = PlayerJoinEvent(player, Component.empty())

        RoosterServices.getIfPresent<PlayerManager>()?.let {
            it.beforePlayerJoin(event)
            it.playerLogin(event.player)
            it.onPlayerJoin(event)
        }

        this.player = player
        return player
    }

    fun command(input: String): Pair<Boolean, Boolean> {
        values.clear()

        val command = input.split(" ").firstOrNull()
        val args = input.substring((command?.length ?: -1) + 1)

        try {
            when (command) {
                "exit" -> {
                    println("Exiting the simulator.")
                    return false to true
                }

                "exit-preserve" -> {
                    return false to false
                }

                "complete" -> {
                    CommandSimulatorHandler.completeCommandTerminal(args)
                }

                "invoke" -> {
                    CommandSimulatorHandler.invokeCommandTerminal(args)
                }

                "open" -> {
                    InterfaceSimulatorHandler.parseOpening(args)
                }

                "show" -> {
                    InterfaceSimulatorHandler.parseShow(args)
                }

                "click" -> {
                    InterfaceSimulatorHandler.parseClick(args)
                }

                else -> println("Unknown command: $input")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true to false
    }

    var isTerminal = false

    fun startTerminal() {
        println("Welcome to the Input Simulator. Type commands to simulate input. Type 'exit' to quit.")

        isTerminal = true

        while (true) {
            print("> ")
            val input = readlnOrNull() ?: continue

            val (continueCommand, deleteDirectory) = command(input)
            if (!continueCommand) {
                if (deleteDirectory) deleteMockDirectory()
                break
            }
        }
    }

    fun deleteMockDirectory() {
        val mockDirectory = Rooster.plugin.dataFolder.parentFile

        if (mockDirectory != null && mockDirectory.exists() && mockDirectory.isDirectory) {
            mockDirectory.deleteRecursively()
            println("Mock directory cleared: ${mockDirectory.absolutePath}")
        } else {
            println("Mock directory not found.")
        }
    }

    private var isSimulating = false
    fun nonTest(block: () -> Unit) {
        if (!isSimulating) {
            block()
        }
    }

    fun onlyTest(block: () -> Unit) {
        if (isSimulating) {
            block()
        }
    }

    val values = mutableMapOf<String, Any>()

    var error: String
        get() {
            return values["error"] as String? ?: "no-error"
        }
        set(error: String) {
            onlyTest {
                values["error"] = error
            }
        }

    var path: String
        get() {
            return values["path"] as String? ?: "no-path"
        }
        set(path: String) {
            onlyTest {
                values["path"] = path
            }
        }

    var interfaceName: String
        get() {
            return values["interfaceName"] as String? ?: ""
        }
        set(interfaceName: String) {
            onlyTest {
                values["interfaceName"] = interfaceName
            }
        }

    fun printValues() {
        println("values: ")
        values.forEach { (key, value) ->
            println("# Key: $key | Value: $value")
        }
    }
}