package de.cypdashuhn.build.actions

import database.utility_tables.attributes.AttributeKey
import de.cypdashuhn.build.BuildPlugin
import de.cypdashuhn.build.db.BuildsManager
import org.bukkit.Location
import org.bukkit.entity.Player

object RegisterBuild {
    data class RegisteringData(val isRegistering: Boolean, val buildName: String?, var pos1Id: Int?, var pos2Id: Int?)

    private val playerRegisteringData =
        AttributeKey.custom("playerRegisteringStep", RegisteringData(false, null, null, null))

    fun registerStart(sender: Player, buildName: String) {
        BuildPlugin.playerAttributeManager.set(
            sender,
            playerRegisteringData,
            RegisteringData(true, buildName, null, null)
        )
    }

    fun registerPos1(sender: Player, pos: Location) =
        registerPos(sender, pos) { data, id -> data.also { it.pos1Id = id } }

    fun registerPos2(sender: Player, pos: Location) =
        registerPos(sender, pos) { data, id -> data.also { it.pos2Id = id } }

    private fun registerPos(sender: Player, pos: Location, modifier: (RegisteringData, Int) -> RegisteringData) {
        var data = BuildPlugin.playerAttributeManager.get(sender, playerRegisteringData)
        assert(data.isRegistering) { "Player is not registering" }

        val location = BuildPlugin.locationManager.insertOrGetLocation(pos, roundCoordinates = true)
        data = modifier(data, location.id.value)

        BuildPlugin.playerAttributeManager.set(sender, playerRegisteringData, data)
    }

    fun registerEnd(sender: Player) {
        val data = BuildPlugin.playerAttributeManager.get(sender, playerRegisteringData)

        assert(data.isRegistering && data.buildName != null && data.pos1Id != null && data.pos2Id != null) { "Player must have filled Register Data" }

        BuildPlugin.playerAttributeManager.clear(sender, playerRegisteringData)

        val pos1 = BuildPlugin.locationManager.locationById(data.pos1Id!!)
        val pos2 = BuildPlugin.locationManager.locationById(data.pos2Id!!)

        assert(listOf(pos1, pos2).none { it == null }) { "Pos1 or Pos2 not found" }

        BuildsManager.register(data.buildName!!, pos1!!, pos2!!)
    }

    fun isPlayerRegistering(sender: Player): Boolean {
        val data = BuildPlugin.playerAttributeManager.get(sender, playerRegisteringData)
        return data.isRegistering
    }

    fun playerHasBothPos(sender: Player): Boolean {
        val data = BuildPlugin.playerAttributeManager.get(sender, playerRegisteringData)
        return data.pos1Id != null && data.pos2Id != null
    }
}