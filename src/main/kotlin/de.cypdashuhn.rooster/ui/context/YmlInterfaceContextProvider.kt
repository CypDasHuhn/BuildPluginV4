package de.cypdashuhn.rooster.ui.context

import com.google.gson.Gson
import de.cypdashuhn.rooster.database.YmlOperations
import de.cypdashuhn.rooster.database.YmlShell
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.entity.Player

class YmlInterfaceContextProvider : InterfaceContextProvider(), YmlOperations by YmlShell("interfaceContexts.yml") {
    override fun <T : Context> updateContext(player: Player, interfaceInstance: Interface<T>, context: T) {
        changeConfig {
            config.set("Players.${player.uuid()}.${interfaceInstance.interfaceName}", Gson().toJson(context))
        }
    }

    override fun <T : Context> getContext(player: Player, interfaceInstance: Interface<T>): T? {
        val jsonString = config.getString("Players.${player.uuid()}.${interfaceInstance.interfaceName}")
        return Gson().fromJson(jsonString, interfaceInstance.contextClass.java)
    }
}