package dev.cypdashuhn.build.commands.anchor
/*
import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.database.utility_tables.LocationManager
import de.cypdashuhn.rooster.localization.t
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull

const val ANCHOR_NAME_KEY = "anchorName"

val create = Arguments.literal.single(t("build_anchor_create"))
    .followedBy(
        Arguments.names.unique(
            key = ANCHOR_NAME_KEY,
            targetColumn = LocationManager.Locations.key as Column<String>,
            extraQuery = LocationManager.Locations.key.isNotNull(),
            uniqueErrorKey = "build.create.name_used_error"
        )
    )*/