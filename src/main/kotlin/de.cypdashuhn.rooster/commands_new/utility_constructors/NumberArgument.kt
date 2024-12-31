package de.cypdashuhn.rooster.commands_new.utility_constructors

import de.cypdashuhn.rooster.commands_new.constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands_new.constructors.InvokeInfo
import de.cypdashuhn.rooster.commands_new.constructors.IsValidResult
import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster.util.location
import org.bukkit.Location
import org.bukkit.World

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
                if (acceptDecimals) it.arg.toDouble()
                else it.arg.toInt()
            },
            onMissing = onMissing,
            suggestions = { listOf(tabCompleterPlaceholder) }
        )
    }

    fun number(
        key: String = "number",
        notANumberErrorMessageKey: String,
        decimalNotAcceptedErrorMessageKey: String? = null,
        negativesNotAcceptedErrorMessageKey: String? = null,
        numArg: String = "num",
        furtherCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        /** set translations to stuff like "\[number]" */
        tabCompleterPlaceholder: String = "rooster.number_placeholder",
        errorMissingMessageKey: String,
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
            onMissing = { it.sender.tSend(errorMissingMessageKey) }
        )
    }

    fun xyzCoordinates(
        keyPreset: String = "",
        xKey: String = "X",
        yKey: String = "Y",
        zKey: String = "Z",
        numberArg: String = "number",
        notANumberErrorMessageKey: String = "rooster.not_a_number",
        /**
         * setting this field to a null value will enable decimals. by default,
         * decimals will not be passed.
         */
        decimalErrorMessageKey: String? = "rooster.decimal_error",
        /**
         * setting this field to a non-null value will disable negatives. by
         * default, negatives will be passed.
         */
        negativesNotAcceptedErrorMessageKey: String? = null,
        errorMissingXMessageKey: String = "rooster.error_missing_num",
        errorMissingYMessageKey: String = "rooster.error_missing_num",
        errorMissingZMessageKey: String = "rooster.error_missing_num",
        xCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        disableYCondition: Boolean = false,
        yCondition: ((ArgumentInfo) -> IsValidResult)? = { (sender, _, arg, _, _) ->
            val num = arg.toDouble()
            when {
                num <= -65.0 -> {
                    IsValidResult.Invalid { sender.tSend("rooster.number_under_build_height_error", numberArg to arg) }
                }

                num >= 321.0 -> {
                    IsValidResult.Invalid {
                        sender.tSend("rooster.number_over_build_height_error", numberArg to arg)
                    }
                }

                else -> {
                    IsValidResult.Valid()
                }
            }
        },
        zCondition: ((ArgumentInfo) -> IsValidResult)? = null
    ): UnfinishedArgument {
        return number(
            key = "$keyPreset$xKey",
            notANumberErrorMessageKey = notANumberErrorMessageKey,
            decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
            negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
            numArg = numberArg,
            errorMissingMessageKey = errorMissingXMessageKey,
            furtherCondition = xCondition
        ).followedBy(
            number(
                key = "$keyPreset$yKey",
                notANumberErrorMessageKey = notANumberErrorMessageKey,
                decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
                numArg = numberArg,
                errorMissingMessageKey = errorMissingYMessageKey,
                furtherCondition = if (disableYCondition) null else yCondition
            ).followedBy(
                number(
                    key = "$keyPreset$zKey",
                    notANumberErrorMessageKey = notANumberErrorMessageKey,
                    decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                    negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
                    numArg = numberArg,
                    errorMissingMessageKey = errorMissingZMessageKey,
                    furtherCondition = zCondition,
                )
            )
        )
    }

    fun locationFromContext(
        argumentInfo: InvokeInfo,
        keyPreset: String = "",
        world: World? = null,
        xKey: String = "X",
        yKey: String = "Y",
        zKey: String = "Z"
    ): Location? {
        val x = argumentInfo.context["$keyPreset$xKey"] as? Int
        val y = argumentInfo.context["$keyPreset$yKey"] as? Int
        val z = argumentInfo.context["$keyPreset$zKey"] as? Int

        if (listOf(x, y, z).any { it == null }) return null
        return Location(world ?: argumentInfo.sender.location()!!.world, x!!.toDouble(), y!!.toDouble(), z!!.toDouble())
    }
}