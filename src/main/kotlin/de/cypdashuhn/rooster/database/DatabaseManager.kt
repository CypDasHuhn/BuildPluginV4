package de.cypdashuhn.rooster.database

import de.cypdashuhn.rooster.core.Rooster
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

fun initDatabase(tables: List<Table>) {
    if (tables.isEmpty()) return

    if (Rooster.databasePath == null) Rooster.databasePath =
        Rooster.plugin.dataFolder.resolve("database.db").absolutePath

    Database.connect("jdbc:sqlite:$databasePath", "org.sqlite.JDBC")

    transaction {
        SchemaUtils.createMissingTablesAndColumns(*tables.toTypedArray())
    }
}

fun <T : IntEntity> IntEntityClass<T>.findEntry(query: Op<Boolean>): T {
    return transaction { this@findEntry.find(query).firstOrNull() }
}