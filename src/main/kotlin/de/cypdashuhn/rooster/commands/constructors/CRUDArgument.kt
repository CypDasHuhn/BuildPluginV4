package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.commands.UnfinishedArgument
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Column

class CRUDArgument<T : IntEntity>(
    entity: IntEntityClass<T>,
    displayField: Column<String>
) {
    val newNameArgument = Arguments.names.unique(entity.table, displayField)
    val nameArgument = Arguments.list.dbList(entity, displayField)

    fun arg(arg: UnfinishedArgument): Argument {
        return arg.followedBy(
            Arguments.literal.single("create").followedBy(newNameArgument).onExecute { },
            Arguments.literal.single("edit").followedBy(nameArgument).onExecute { },
            Arguments.literal.single("delete").followedBy(nameArgument).onExecute { },
            Arguments.literal.single("get").followedBy(nameArgument).onExecute { },
        )
    }
}