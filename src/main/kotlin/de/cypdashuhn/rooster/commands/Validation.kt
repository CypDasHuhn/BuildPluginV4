package de.cypdashuhn.rooster.commands

import de.cypdashuhn.rooster.localization.tSend

fun ((ArgumentInfo) -> IsValidResult)?.toRule(argumentInfo: ArgumentInfo): Pair<ArgumentRule, () -> Boolean> {
    if (this == null) return ArgumentRule.Accepted to { false }

    val result = this(argumentInfo)

    if (result is IsValidResult.Valid) return ArgumentRule.Accepted to { false }

    return ArgumentRule.NotAccepted(result.error!!) to { true }
}

class Rules(vararg rules: Pair<ArgumentRule, () -> Boolean>, private val further: (() -> Rules)? = null) {
    private var rules: MutableList<Pair<ArgumentRule, () -> Boolean>> = rules.toMutableList()

    fun result(): IsValidResult {
        for ((rule, condition) in rules) {
            if (rule is ArgumentRule.NotAccepted && condition()) return rule.errorResult()
        }

        if (further != null) {
            return further.let { it() }.result()
        }
        return IsValidResult.Valid()
    }
}

sealed class IsValidResult(
    val isValid: Boolean,
    val error: ((ArgumentInfo) -> Unit)? = null
) {
    class Valid : IsValidResult(true, null)
    class Invalid(error: ((ArgumentInfo) -> Unit)) : IsValidResult(false, error)

    fun toRule(): ArgumentRule = when (this) {
        is Valid -> ArgumentRule.Accepted
        is Invalid -> ArgumentRule.NotAccepted(error!!)
    }
}

sealed class ArgumentRule {
    data object Accepted : ArgumentRule()
    data class NotAccepted(val error: ((ArgumentInfo) -> Unit)) : ArgumentRule() {
        fun errorResult() = IsValidResult.Invalid(error)
    }

    companion object {
        fun create(messageKey: String?): ArgumentRule {
            return if (messageKey == null) Accepted
            else NotAccepted(playerMessage(messageKey))
        }

        fun create(error: ((ArgumentInfo) -> Unit)?): ArgumentRule {
            return if (error == null) Accepted
            else NotAccepted(error)
        }
    }
}

fun playerMessage(messageKey: String, arg: String = "arg"): (ArgumentInfo) -> Unit = {
    it.sender.tSend(messageKey, arg to it.arg)
}

fun <T> playerMessageExtra(messageKey: String, arg: String = "arg"): (ArgumentInfo, T) -> Unit = { info, field ->
    info.sender.tSend(messageKey, arg to field.toString())
}