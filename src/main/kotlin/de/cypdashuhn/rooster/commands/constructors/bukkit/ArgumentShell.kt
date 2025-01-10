package de.cypdashuhn.rooster.commands.constructors.bukkit

import de.cypdashuhn.rooster.commands.*

abstract class ArgumentShell <T> protected constructor(
    val defaultKey: String,
    val argumentName: String,
    val onMissingKey: String,
    val onInvalidKey: String,
    val messageArg: String
) {
    abstract fun getOptions(): List<T>
    abstract fun adaptString(string: String): T
    abstract fun adaptValue(value: T): String

    fun single(
        key: String = defaultKey,
        onMissing: (ArgumentInfo) -> Unit = playerMessage(onMissingKey),
        onInvalid: (ArgumentInfo) -> Unit = playerMessage(onInvalidKey, messageArg),
        filter: (T) -> Boolean = { true },
    ): SimpleArgumentType<T> {
        val arg = Arguments.list.single(
            list = getOptions().filter(filter).map(::adaptValue),
            onMissing = onMissing,
            notMatchingError = { info, _ -> onInvalid(info) }
        )

        return SimpleArgumentType(argumentName, arg, key)
    }

    fun multiple(
        key: String = defaultKey,
        onMissing: (ArgumentInfo) -> Unit = playerMessage(onMissingKey),
        onInvalid: (ArgumentInfo) -> Unit = playerMessage(onInvalidKey, messageArg),
        filter: (T) -> Boolean = { true },
    ): SimpleArgumentType<List<T>> {
        val arg = Arguments.list.single(
            list = getOptions().filter(filter).map(::adaptValue),
            onMissing = onMissing,
            notMatchingError = { info, _ -> onInvalid(info) }
        )

        return SimpleArgumentType(argumentName, arg, key)
    }
}