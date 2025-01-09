package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.localization.language
import de.cypdashuhn.rooster.localization.transformMessage

object LiteralArgument {
    fun single(
        name: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate = { transformMessage(name, it.sender.language()).startsWith(it.arg) },
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any) = { it.arg },
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        key: String = name,
    ): LiteralArgumentType {
        val arg = UnfinishedArgument(
            key = key,
            suggestions = { listOf(name) },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            onArgumentOverflow = onArgumentOverflow,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            isOptional = false
        )

        return LiteralArgumentType(arg, key)
    }

    fun multiple(
        names: List<String>,
        key: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate = { names.contains(it.arg) },
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any) = { it.arg },
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    ): LiteralArgumentType {
        val arg = UnfinishedArgument(
            key = key,
            suggestions = { names },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            onArgumentOverflow = onArgumentOverflow,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            isOptional = false
        )

        return LiteralArgumentType(arg, key)
    }

    class LiteralArgumentType(arg: UnfinishedArgument, argKey: String) :
        SimpleArgumentType<String>("Literal", arg, argKey)
}