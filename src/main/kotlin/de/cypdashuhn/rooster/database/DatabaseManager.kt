package de.cypdashuhn.rooster.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

fun initDatabase(tables: List<Table>, databasePath: String) {
    if (tables.isEmpty()) return

    Database.connect("jdbc:sqlite:$databasePath", "org.sqlite.JDBC")

    transaction {
        SchemaUtils.createMissingTablesAndColumns(*tables.toTypedArray())
    }
}

fun <T : IntEntity> IntEntityClass<T>.findEntry(query: Op<Boolean>): T? {
    return transaction { this@findEntry.find(query).firstOrNull() }
}