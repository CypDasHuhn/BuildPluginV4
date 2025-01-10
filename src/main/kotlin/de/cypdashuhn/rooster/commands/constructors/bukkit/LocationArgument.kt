package de.cypdashuhn.rooster.commands.constructors.bukkit

import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.commands.constructors.NumberArgument
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster.region.Region
import de.cypdashuhn.rooster.util.location
import org.bukkit.Location
import org.bukkit.command.CommandSender

object LocationArgument {
    fun location(
        keyPreset: String = "",
        xKey: String = "X",
        yKey: String = "Y",
        zKey: String = "Z",
        numberArg: String = "number",
        notANumberErrorMessageKey: String = "rooster.location.not_a_number",
        /**
         * setting this field to a null value will enable decimals. by default,
         * decimals will not be passed.
         */
        decimalErrorMessageKey: String? = "rooster.location.decimal_error",
        /**
         * setting this field to a non-null value will disable negatives. by
         * default, negatives will be passed.
         */
        negativesNotAcceptedErrorMessageKey: String? = null,
        errorMissingXMessageKey: String = "rooster.location.missing.x",
        errorMissingYMessageKey: String = "rooster.location.missing.x",
        errorMissingZMessageKey: String = "rooster.location.missing.x",
        xCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        disableYCondition: Boolean = false,
        yCondition: ((ArgumentInfo) -> IsValidResult)? = { (sender, _, arg, _, _) ->
            val num = arg.toDouble()
            when {
                num <= -65.0 -> {
                    IsValidResult.Invalid { sender.tSend("rooster.location.y.too_low", numberArg to arg) }
                }

                num >= 321.0 -> {
                    IsValidResult.Invalid {
                        sender.tSend("rooster.location.y.too_high", numberArg to arg)
                    }
                }

                else -> {
                    IsValidResult.Valid()
                }
            }
        },
        zCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        xTransformValue: ((ArgumentInfo, Int) -> Int) = { _, num -> num },
        yTransformValue: ((ArgumentInfo, Int) -> Int) = { _, num -> num },
        zTransformValue: ((ArgumentInfo, Int) -> Int) = { _, num -> num },
        xTabCompletePlaceholder: String = "[X]",
        yTabCompletePlaceholder: String = "[Y]",
        zTabCompletePlaceholder: String = "[Z]"
    ): LocationArgumentType {
        val arg = NumberArgument.integer(
            key = "$keyPreset$xKey",
            tabCompleterPlaceholder = xTabCompletePlaceholder,
            decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
            furtherCondition = xCondition,
            transformValue = xTransformValue,
            notANumberError = playerMessage(notANumberErrorMessageKey, numberArg),
            negativeRule = ArgumentRule.create(negativesNotAcceptedErrorMessageKey),
            onMissing = playerMessage(errorMissingXMessageKey)
        ).followedBy(
            NumberArgument.integer(
                key = "$keyPreset$yKey",
                tabCompleterPlaceholder = yTabCompletePlaceholder,
                decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                furtherCondition = if (disableYCondition) null else yCondition,
                transformValue = yTransformValue,
                notANumberError = playerMessage(notANumberErrorMessageKey, numberArg),
                negativeRule = ArgumentRule.create(negativesNotAcceptedErrorMessageKey),
                onMissing = playerMessage(errorMissingYMessageKey)
            ).followedBy(
                NumberArgument.integer(
                    key = "$keyPreset$zKey",
                    tabCompleterPlaceholder = zTabCompletePlaceholder,
                    decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                    furtherCondition = zCondition,
                    transformValue = zTransformValue,
                    notANumberError = playerMessage(notANumberErrorMessageKey, numberArg),
                    negativeRule = ArgumentRule.create(negativesNotAcceptedErrorMessageKey),
                    onMissing = playerMessage(errorMissingZMessageKey)
                )
            )
        )

        return LocationArgumentType(arg, keyPreset, xKey, yKey, zKey)
    }

    fun region(
        keyPreset: String = "",
    ): RegionArgumentType {
        val loc1Arg = location(keyPreset = "${keyPreset}first_")
        val loc2Arg = location(keyPreset = "${keyPreset}second_")

        return RegionArgumentType(loc1Arg.followedBy(loc2Arg), loc1Arg, loc2Arg)
    }
}

class LocationArgumentType(
    arg: UnfinishedArgument,
    var keyPreset: String,
    var xKey: String,
    var yKey: String,
    var zKey: String
) : TypedArgument<Location>(arg) {
    override fun value(sender: CommandSender, context: CommandContext): TypeResult<Location> {
        fun num(key: String): Double? {
            return context["$keyPreset$key"]?.let {
                return@num (it as? Int)?.toDouble() ?: it as Double
            }
        }
        val (x, y, z) = listOf(xKey, yKey, zKey).map(::num)

        if (listOf(x, y, z).any { it == null }) return TypeResult.Failure(
            IllegalStateException("X, Y or Z is null for Location Argument")
        )

        return TypeResult.Success(Location(sender.location()!!.world, x!!, y!!, z!!))
    }
}

class RegionArgumentType(
    arg: UnfinishedArgument,
    var loc1Arg: LocationArgumentType,
    var loc2Arg: LocationArgumentType,
) : TypedArgument<Region>(arg) {
    override fun value(sender: CommandSender, context: CommandContext): TypeResult<Region> {
        val invokeInfo = InvokeInfo(sender, context, listOf())

        val loc1 = invokeInfo.argNullable(loc1Arg)
        val loc2 = invokeInfo.argNullable(loc2Arg)
        if (listOf(
                loc1,
                loc2
            ).any { it == null }
        ) return TypeResult.Failure(IllegalStateException("Loc1 or Loc2 is null for Region Argument"))
        return TypeResult.Success(Region(loc1!!, loc2!!))
    }
}