package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.ArgumentInfo
import de.cypdashuhn.rooster.commands.IsValidResult
import de.cypdashuhn.rooster.commands.UnfinishedArgument
import de.cypdashuhn.rooster.localization.tSend

object NumberArgument {
    fun number(
        key: String = "number",
        notANumberError: (ArgumentInfo) -> Unit,
        acceptDecimals: Boolean = false,
        decimalNotAcceptedError: (ArgumentInfo) -> Unit,
        acceptNegatives: Boolean = false,
        negativesNotAcceptedError: (ArgumentInfo) -> Unit,
        furtherCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        tabCompleterPlaceholder: String = "rooster.number_placeholder", /* set translations to stuff like "[number]" */
        onMissing: (ArgumentInfo) -> Unit,
        transformValue: (ArgumentInfo, Double) -> Double = { _, num -> num }
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            isValid = { argInfo ->
                val arg = argInfo.arg

                val doubleNum = arg.toDoubleOrNull() ?: return@UnfinishedArgument IsValidResult.Invalid(notANumberError)

                if (!acceptDecimals && doubleNum % 1.0 != 0.0) {
                    return@UnfinishedArgument IsValidResult.Invalid(decimalNotAcceptedError)
                }
                if (!acceptNegatives && doubleNum < 0) {
                    return@UnfinishedArgument IsValidResult.Invalid(negativesNotAcceptedError)
                }

                furtherCondition?.let {
                    return@UnfinishedArgument furtherCondition(argInfo)
                }

                IsValidResult.Valid()
            },
            transformValue = {
                val num = it.arg.toDouble()

                transformValue(it, num)
            },
            onMissing = onMissing,
            suggestions = { listOf(tabCompleterPlaceholder) }
        )
    }

    fun number(
        key: String = "number",
        notANumberErrorMessageKey: String = "rooster.not_a_number",
        decimalNotAcceptedErrorMessageKey: String? = null,
        negativesNotAcceptedErrorMessageKey: String? = null,
        numArg: String = "num",
        furtherCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        /** set translations to stuff like "\[number]" */
        tabCompleterPlaceholder: String = "rooster.number_placeholder",
        errorMissingMessageKey: String = "rooster.error_missing_num",
        transformValue: (ArgumentInfo, Double) -> Double = { _, num -> num }
    ): UnfinishedArgument {
        return number(
            key = key,
            notANumberError = { it.sender.tSend(notANumberErrorMessageKey, numArg to it.arg) },
            acceptDecimals = decimalNotAcceptedErrorMessageKey == null,
            decimalNotAcceptedError = { it.sender.tSend(decimalNotAcceptedErrorMessageKey ?: "", numArg to it.arg) },
            acceptNegatives = negativesNotAcceptedErrorMessageKey == null,
            negativesNotAcceptedError = {
                it.sender.tSend(
                    negativesNotAcceptedErrorMessageKey
                        ?: throw IllegalStateException("negativesNotAcceptedErrorMessageKey is null"),
                    numArg to it.arg
                )
            },
            furtherCondition = furtherCondition,
            tabCompleterPlaceholder = tabCompleterPlaceholder,
            onMissing = { it.sender.tSend(errorMissingMessageKey) },
            transformValue = transformValue
        )
    }
}