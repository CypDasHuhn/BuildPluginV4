package de.cypdashuhn.rooster.commands_new.constructors

class UnfinishedArgument : BaseArgument {
    constructor(
        key: String,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        suggestions: ((ArgumentInfo) -> List<String>)? = null,
        onExecute: ((InvokeInfo) -> Unit)? = null,
        followedBy: List<BaseArgument>? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any) = { it.arg },
        isOptional: Boolean = false,
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    ) : super(
        key = key,
        isEnabled = isEnabled,
        isTarget = isTarget,
        suggestions = suggestions,
        onExecute = onExecute,
        followedBy = followedBy?.toMutableList(),
        isValid = isValid,
        onMissing = onMissing,
        onMissingChild = onMissingChild,
        transformValue = transformValue,
        isOptional = isOptional,
        onArgumentOverflow = onArgumentOverflow,
    )

    private constructor(
        key: String,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        suggestions: ((ArgumentInfo) -> List<String>)? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        transformValue: ((ArgumentInfo) -> Any) = { it.arg },
    ) : super(
        key = key,
        isEnabled = isEnabled,
        isTarget = isTarget,
        suggestions = suggestions,
        onExecute = null,
        followedBy = null,
        isValid = isValid,
        onMissing = null,
        onMissingChild = null,
        transformValue = transformValue,
        isOptional = true,
        onArgumentOverflow = null,
    )

    internal companion object {
        fun optional(
            key: String,
            isEnabled: (ArgumentPredicate)? = { true },
            isTarget: (ArgumentPredicate) = { true },
            suggestions: ((ArgumentInfo) -> List<String>)? = null,
            isValid: ((ArgumentInfo) -> IsValidResult)? = null,
            transformValue: ((ArgumentInfo) -> Any) = { it.arg },
        ) {
            UnfinishedArgument(
                key = key,
                isEnabled = isEnabled,
                isTarget = isTarget,
                suggestions = suggestions,
                isValid = isValid,
                transformValue = transformValue,
            )
        }
    }
}