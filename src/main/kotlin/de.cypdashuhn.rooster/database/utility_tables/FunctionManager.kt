package de.cypdashuhn.rooster.database.utility_tables

import de.cypdashuhn.rooster.core.Rooster
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.reflect.Method

class FunctionManager : UtilityDatabase(Functions) {
    object Functions : IntIdTable("RoosterFunctions") {
        val key = varchar("name", 255)
    }

    class Function(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Function>(Functions)

        var key by Functions.key
    }

    fun addKey(key: String): Function {
        return transaction {
            Function.new {
                this.key = key
            }
        }
    }

    fun getFunction(key: String): Method? {
        return Rooster.registeredFunctions[key]
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RoosterLambda(val key: String)