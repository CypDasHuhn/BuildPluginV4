package de.cypdashuhn.build

import database.utility_tables.attributes.PlayerAttributeManager
import de.cypdashuhn.rooster.core.RoosterPlugin
import de.cypdashuhn.rooster.database.utility_tables.LocationManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager

class BuildPlugin : RoosterPlugin("BuildPlugin") {
    companion object {
        lateinit var playerManager: PlayerManager
        lateinit var playerAttributeManager: PlayerAttributeManager
        lateinit var locationManager: LocationManager
    }

    override fun beforeInitialize() {
        playerManager = PlayerManager()
        playerAttributeManager = PlayerAttributeManager()
        locationManager = LocationManager()
    }

    override fun onInitialize() {

    }
}