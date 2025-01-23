package de.cypdashuhn.build.commands.anchor

import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.database.utility_tables.LocationManager
import de.cypdashuhn.rooster.localization.t
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull

const val ANCHOR_NAME_KEY = "anchorName"

val create = Arguments.literal.single(t("build_anchor_create"))
    .followedBy(
        Arguments.names.unique(
            key = ANCHOR_NAME_KEY,
            table = LocationManager.Locations,
            targetColumn = LocationManager.Locations.key as Column<String>,
            extraQuery = LocationManager.Locations.key.isNotNull() and,
            uniqueErrorKey = "build.create.name_used_error"
        )
    )