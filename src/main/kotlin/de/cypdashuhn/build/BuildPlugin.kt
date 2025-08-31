package de.cypdashuhn.build

import de.cypdashuhn.build.commands.build.CreateCommand
import de.cypdashuhn.build.commands.build.DeleteCommand
import de.cypdashuhn.build.commands.build.EditCommand
import de.cypdashuhn.build.commands.build.LoadCommand
import de.cypdashuhn.rooster.core.RoosterPlugin
import de.cypdashuhn.rooster.core.RoosterServices
import de.cypdashuhn.rooster.database.utility_tables.LocationManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.attributes.PlayerAttributeManager
import de.cypdashuhn.rooster.localization.provider.LocaleProvider
import de.cypdashuhn.rooster.localization.provider.SqlLocaleProvider
import java.util.*

class BuildPlugin : RoosterPlugin("BuildPlugin") {
    companion object {
        val playerManager by RoosterServices.setDelegate(PlayerManager())
        val playerAttributeManager by RoosterServices.setDelegate(PlayerAttributeManager())
        val locationManager by RoosterServices.setDelegate(LocationManager())
    }

    override fun beforeInitialize() {
        CreateCommand
        DeleteCommand
        EditCommand
        LoadCommand
        

        RoosterServices.set<LocaleProvider>(
            SqlLocaleProvider(
                mapOf(
                    "en_US" to Locale.ENGLISH,
                    "de_DE" to Locale.GERMAN
                ), "en_US"
            )
        )
    }

    override fun onInitialize() {

    }
}