package de.cypdashuhn.build.db

import de.cypdashuhn.rooster.database.utility_tables.LocationManager
import org.jetbrains.exposed.dao.id.IntIdTable

object AnchorManager {
    object Anchors : IntIdTable() {
        val location = reference("location", LocationManager.Locations)
    }

    object RegionAnchors : IntIdTable() {
        val location = reference("location", LocationManager.Locations)
    }
}