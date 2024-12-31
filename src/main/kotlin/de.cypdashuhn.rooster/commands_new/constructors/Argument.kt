package de.cypdashuhn.rooster.commands_new.constructors

class Argument : BaseArgument {
    constructor(
        key: String,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        suggestions: ((ArgumentInfo) -> List<String>)? = null,
        onExecute: (InvokeInfo) -> Unit,
        followedBy: List<Argument>? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any) = { it.arg },
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        toBeDeleted: Boolean = false
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
        isOptional = false,
        onArgumentOverflow = onArgumentOverflow,
    )

    constructor(
        key: String,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        suggestions: ((ArgumentInfo) -> List<String>)? = null,
        onExecute: ((InvokeInfo) -> Unit)? = null,
        followedBy: List<Argument>,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any) = { it.arg },
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    ) : super(
        key = key,
        isEnabled = isEnabled,
        isTarget = isTarget,
        suggestions = suggestions,
        onExecute = onExecute,
        followedBy = followedBy.toMutableList(),
        isValid = isValid,
        onMissing = onMissing,
        onMissingChild = onMissingChild,
        transformValue = transformValue,
        isOptional = false,
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
        ) = Argument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            suggestions = suggestions,
            isValid = isValid,
            transformValue = transformValue,
        )
    }

    fun or(alternative: Argument): Argument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = mutableListOf(alternative)
            } else {
                it.followedBy!!.add(alternative)
            }
        } as Argument
    }

    fun or(vararg alternatives: Argument): Argument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = alternatives.toMutableList()
            } else {
                it.followedBy!!.addAll(alternatives)
            }
        } as Argument
    }

    fun or(alternatives: List<Argument>): Argument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = alternatives.toMutableList()
            } else {
                it.followedBy!!.addAll(alternatives)
            }
        } as Argument
    }

    fun or(alternative: UnfinishedArgument): UnfinishedArgument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = mutableListOf(alternative)
            } else {
                it.followedBy!!.add(alternative)
            }
        }.toUnfinishedArgument()
    }
}