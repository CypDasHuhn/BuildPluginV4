package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.*

object NumberArgument {
    fun integer(
        key: String = "number",
        notANumberError: (ArgumentInfo) -> Unit = errorMessage("rooster.number.not_a_number_error"),
        negativeRule: ArgumentRule = ArgumentRule.NotAccepted(errorMessage("rooster.number.negative_error")),
        zeroRule: ArgumentRule = ArgumentRule.NotAccepted(errorMessage("rooster.number.zero_error")),
        decimalNotAcceptedErrorMessageKey: String? = "rooster.number.decimal_error",
        furtherCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        /** set translations to stuff like "\[number]" */
        tabCompleterPlaceholder: String = "rooster.number.placeholder",
        onMissing: (ArgumentInfo) -> Unit = errorMessage("rooster.number.missing_error"),
        transformValue: (ArgumentInfo, Int) -> Int = { _, num -> num }
    ): IntegerArgumentType {
        val arg = UnfinishedArgument(
            key = key,
            isValid = { argInfo ->
                val arg = argInfo.arg

                val rules = Rules(
                    ArgumentRule.create(decimalNotAcceptedErrorMessageKey) to {
                        arg.toDoubleOrNull() != null && arg.contains(
                            '.'
                        )
                    },
                    ArgumentRule.create(notANumberError) to { arg.toIntOrNull() == null }
                ) {
                    val intNum = arg.toInt()
                    Rules(
                        negativeRule to { intNum < 0 },
                        zeroRule to { intNum == 0 },
                        furtherCondition.toRule(argInfo)
                    )
                }

                rules.result()
            },
            transformValue = {
                val num = it.arg.toInt()

                transformValue(it, num)
            },
            onMissing = onMissing,
            suggestions = { listOf(tabCompleterPlaceholder) }
        )
        return IntegerArgumentType(arg, key)
    }

    class IntegerArgumentType(
        arg: UnfinishedArgument,
        argKey: String
    ) : SimpleArgumentType<Int>("Integer", arg, argKey)

    fun double(
        key: String = "number",
        notANumberError: (ArgumentInfo) -> Unit = errorMessage("rooster.number.not_a_number_error"),
        negativeRule: ArgumentRule = ArgumentRule.NotAccepted(errorMessage("rooster.number.negative_error")),
        zeroRule: ArgumentRule = ArgumentRule.Accepted,
        furtherCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        /** set translations to stuff like "\[number]" */
        tabCompleterPlaceholder: String = "rooster.number.placeholder",
        onMissing: (ArgumentInfo) -> Unit,
        transformValue: (ArgumentInfo, Double) -> Double = { _, num -> num }
    ): DoubleArgumentType {
        val arg = UnfinishedArgument(
            key = key,
            isValid = { argInfo ->
                val arg = argInfo.arg

                val rules = Rules(
                    ArgumentRule.create(notANumberError) to { arg.toDoubleOrNull() == null }
                ) {
                    val intNum = arg.toDouble()
                    Rules(
                        negativeRule to { intNum < 0 },
                        furtherCondition.toRule(argInfo)
                    )
                }

                rules.result()
            },
            transformValue = {
                val num = it.arg.toDouble()

                transformValue(it, num)
            },
            onMissing = onMissing,
            suggestions = { listOf(tabCompleterPlaceholder) }
        )
        return DoubleArgumentType(arg, key)
    }

    class DoubleArgumentType(
        arg: UnfinishedArgument,
        argKey: String
    ) : SimpleArgumentType<Double>("Double", arg, argKey)
}