package de.cypdashuhn.rooster.database.utility_tables.attributes

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import de.cypdashuhn.rooster.database.utility_tables.UtilityDatabase
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AttributeManager<T : Any?, E : Any>(private val table: Attributes) : UtilityDatabase(table) {
    private val gson = Gson()
    private val attributeKeyManager = AttributeKeyManager()

    protected abstract fun fieldInfo(value: T): Pair<Column<E>, E>
    private val valueToQuery: (T) -> Op<Boolean> by lazy {
        { fieldInfo(it).first eq fieldInfo(it).second }
    }

    private fun initializeNewField(statement: InsertStatement<*>, tableKey: T) {
        statement[fieldInfo(tableKey).first] = fieldInfo(tableKey).second
    }

    open class Attributes(tableName: String) : IntIdTable(tableName) {
        val attributeKey =
            reference("attribute_key", AttributeKeyManager.AttributeKeys, onDelete = ReferenceOption.CASCADE)
        val attributeValue = text("attribute_value")
    }

    fun <K : Any?> set(tableKey: T, key: AttributeKey<K>, value: K?) {
        val attributeKeyId = attributeKeyManager.getAttributeKey(key.key).id
        val jsonValue = gson.toJson(value)
        transaction {
            table.deleteWhere { valueToQuery(tableKey) and (table.attributeKey eq attributeKeyId) }

            table.insert {
                it[attributeKey] = attributeKeyId
                it[attributeValue] = jsonValue
                initializeNewField(it, tableKey)
            }
        }
    }

    fun <K : Any> get(tableKey: T, key: AttributeKey<K>): K {
        val attributeKeyId = attributeKeyManager.getAttributeKey(key.key).id

        return transaction {
            val dbEntry = table.selectAll().where { valueToQuery(tableKey) and (table.attributeKey eq attributeKeyId) }
                .firstOrNull()

            if (dbEntry != null) {
                try {
                    gson.fromJson(dbEntry[table.attributeValue], key.type) ?: key.default!!
                } catch (e: JsonSyntaxException) {
                    key.default!!
                }
            } else {
                key.default!!
            }
        }
    }

    fun <K : Any?> getNullable(tableKey: T, key: AttributeKey<K>): K? {
        val attributeKeyId = attributeKeyManager.getAttributeKey(key.key).id

        return transaction {
            val dbEntry = table.selectAll().where { valueToQuery(tableKey) and (table.attributeKey eq attributeKeyId) }
                .firstOrNull()

            if (dbEntry != null) {
                try {
                    gson.fromJson(dbEntry[table.attributeValue], key.type) ?: key.default
                } catch (e: JsonSyntaxException) {
                    key.default
                }
            } else {
                key.default
            }
        }
    }

    fun <K : Any?> clear(tableKey: T, key: AttributeKey<K>) {
        val attributeKeyId = attributeKeyManager.getAttributeKey(key.key).id
        transaction {
            table.deleteWhere { valueToQuery(tableKey) and (table.attributeKey eq attributeKeyId) }
        }
    }
}