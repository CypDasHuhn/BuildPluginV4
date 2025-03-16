package de.cypdashuhn.rooster.database.utility_tables.attributes

import de.cypdashuhn.rooster.database.utility_tables.UtilityDatabase
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

class AttributeKey<T : Any?>(
    val key: String,
    val type: Class<T>,
    val default: T?
) {
    companion object {
        inline fun <reified T : Any> custom(key: String, default: T): AttributeKey<T> {
            return AttributeKey(key, T::class.java, default)
        }

        inline fun <reified T : Any?> customNullable(key: String, default: T? = null): AttributeKey<T> {
            return AttributeKey(key, T::class.java, default)
        }

        fun boolean(key: String): AttributeKey<Boolean> {
            return AttributeKey(key, Boolean::class.java, false)
        }
    }
}

class AttributeKeyManager : UtilityDatabase(AttributeKeys) {
    object AttributeKeys : IntIdTable("RoosterAttributeKeys") {
        val key = varchar("key", 255).uniqueIndex()
    }

    class DbAttributeKey(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<DbAttributeKey>(AttributeKeys)

        var key: String by AttributeKeys.key
    }

    fun getAttributeKey(key: String): DbAttributeKey {
        return transaction {
            DbAttributeKey
                .find { AttributeKeys.key eq key }.firstOrNull() ?: DbAttributeKey.new {
                this.key = key
            }
        }
    }
}