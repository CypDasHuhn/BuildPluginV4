package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.ArgumentInfo
import de.cypdashuhn.rooster.commands.ArgumentPredicate
import de.cypdashuhn.rooster.commands.IsValidResult
import de.cypdashuhn.rooster.commands.UnfinishedArgument
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
    ): UnfinishedArgument {
        return UnfinishedArgument(
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
    ): UnfinishedArgument {
        return UnfinishedArgument(
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
    }
}