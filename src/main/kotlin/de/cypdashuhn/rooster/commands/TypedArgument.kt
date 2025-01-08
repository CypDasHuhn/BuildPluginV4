package de.cypdashuhn.rooster.commands

import org.bukkit.command.CommandSender

abstract class TypedArgument<T>(
    argument: UnfinishedArgument,
) : UnfinishedArgument(
    key = argument.key,
    isEnabled = argument.isEnabled,
    isTarget = argument.isTarget,
    suggestions = argument.suggestions,
    onExecute = argument.onExecute,
    followedBy = argument.followedBy,
    isValid = argument.isValid,
    onMissing = argument.onMissing,
    onMissingChild = argument.onMissingChild,
    transformValue = argument.transformValue,
    isOptional = argument.isOptional,
    onArgumentOverflow = argument.onArgumentOverflow,
) {
    abstract fun value(sender: CommandSender, context: CommandContext): TypeResult<T>

    fun <T> onExecuteWithThis(onExecuteCallback: (InvokeInfo, TypedArgument<T>) -> Unit): TypedArgument<T> {
        return this.onExecuteTyped { onExecuteCallback(it, this as TypedArgument<T>) }
    }


    fun <T> onExecuteTyped(onExecute: ((InvokeInfo) -> Unit)): TypedArgument<T> {
        return appendChange { it.onExecute = onExecute } as TypedArgument<T>
    }
}

fun <T> List<TypedArgument<T>>.eachOnExecuteWithThis(onExecuteCallback: (InvokeInfo, TypedArgument<T>) -> Unit): List<TypedArgument<T>> {
    return this.map { arg -> arg.onExecuteTyped { onExecuteCallback(it, arg) } }
}

sealed class TypeResult<T> {
    class Success<T>(val value: T) : TypeResult<T>()
    class Failure<T>(val exception: Exception, val action: () -> Unit = {}) : TypeResult<T>()
}