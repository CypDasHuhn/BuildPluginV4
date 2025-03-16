package de.cypdashuhn.rooster.database.utility_tables

import com.google.gson.Gson
import de.cypdashuhn.rooster.database.utility_tables.attributes.AttributeKeyManager.DbAttributeKey.Companion.transform
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class ItemManager : UtilityDatabase(Items) {
    object Items : IntIdTable("RoosterItems") {
        val itemStack = text("item")
        val key = varchar("key", 50).nullable()

        val transformedItem = itemStack.transform(
            { itemStack -> Gson().toJson(itemStack.serialize()) },
            { json -> ItemStack.deserialize(Gson().fromJson(json, Map::class.java) as Map<String, Any>) }
        )
    }

    class Item(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Item>(Items)

        var itemStack: ItemStack by Items.transformedItem

        var key: String? by Items.key
    }

    fun upsertItem(itemStack: ItemStack, key: String? = null, ignoreKeyItems: Boolean = false): Item {
        return transaction {
            val itemStackJson = Gson().toJson(itemStack.serialize())

            var query = Items.itemStack eq itemStackJson
            if (!ignoreKeyItems && key != null) query = query and (Items.key eq key)
            val item = Item.find { query }.firstOrNull()

            if (item != null) return@transaction item

            Item.new {
                this.itemStack = itemStack
                this.key = key
            }
        }
    }

    fun itemByKey(key: String): ItemStack? {
        return transaction {
            Item.find { Items.key eq key }.firstOrNull()?.itemStack
        }
    }
}