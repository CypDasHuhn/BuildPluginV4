package de.cypdashuhn.rooster.commands_new.utility_constructors.bukkit

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import de.cypdashuhn.build.actions.BuildManager.toLocation
import de.cypdashuhn.rooster.commands_new.constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands_new.constructors.InvokeInfo
import de.cypdashuhn.rooster.commands_new.constructors.IsValidResult
import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.NumberArgument
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster.util.location
import org.bukkit.Location
import org.bukkit.World

object LocationArgument {
    fun location(
        key: String = "location",
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
        zCondition: ((ArgumentInfo) -> IsValidResult)? = null,
        xTransformValue: ((ArgumentInfo, Double) -> Double) = { _, num -> num },
        yTransformValue: ((ArgumentInfo, Double) -> Double) = { _, num -> num },
        zTransformValue: ((ArgumentInfo, Double) -> Double) = { _, num -> num },
    ): UnfinishedArgument {
        return NumberArgument.number(
            key = "$keyPreset$xKey",
            notANumberErrorMessageKey = notANumberErrorMessageKey,
            decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
            negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
            numArg = numberArg,
            errorMissingMessageKey = errorMissingXMessageKey,
            furtherCondition = xCondition,
            transformValue = xTransformValue
        ).followedBy(
            NumberArgument.number(
                key = "$keyPreset$yKey",
                notANumberErrorMessageKey = notANumberErrorMessageKey,
                decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
                numArg = numberArg,
                errorMissingMessageKey = errorMissingYMessageKey,
                furtherCondition = if (disableYCondition) null else yCondition,
                transformValue = yTransformValue
            ).followedBy(
                NumberArgument.number(
                    key = "$keyPreset$zKey",
                    notANumberErrorMessageKey = notANumberErrorMessageKey,
                    decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                    negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
                    numArg = numberArg,
                    errorMissingMessageKey = errorMissingZMessageKey,
                    furtherCondition = zCondition,
                    transformValue = { info, num ->
                        fun num(key: String): Double {
                            info.context["$keyPreset$key"]?.let {
                                return@num (it as? Int)?.toDouble() ?: it as Double
                            } ?: throw IllegalStateException("$key for Location Argument is null")
                        }
                        val (x, y, z) = listOf(xKey, yKey, zKey).map(::num)

                        info.context["$keyPreset$key"] = Location(info.sender.location()!!.world, x, y, z)

                        zTransformValue(info, num)
                    }
                )
            )
        )
    }

    fun region(
        key: String = "region",
        keyPreset: String = "",
    ): UnfinishedArgument {
        return location(
            keyPreset = "${keyPreset}first_"
        ).followedBy(location(
            keyPreset = "${keyPreset}second_",
            zTransformValue = { info, num ->
                val loc1 = info.context["${keyPreset}first_location"] as? Location
                val loc2 = info.context["${keyPreset}second_location"] as? Location
                if (listOf(loc1, loc2).any { it == null }) throw IllegalStateException("Loc1 or Loc2 is null for Region Argument")
                info.context[key] = (loc1!! to loc2!!).toWorldEditRegion()
                num
            }
        ))
    }
}

class CommandContext(private val innerMap: MutableMap<String, Any> = mutableMapOf()) {
    operator fun get(key: String): Any? = innerMap[key]

    operator fun set(key: String, value: Any) {
        innerMap[key] = value
    }

    fun putIfAbsent(key: String, value: Any) = innerMap.putIfAbsent(key, value)

    fun toMap(): Map<String, Any> = innerMap.toMap()

    override fun toString(): String = innerMap.toString()
}

fun Pair<Location, Location>.toWorldEditRegion(): CuboidRegion {
    return CuboidRegion(BukkitAdapter.adapt(first.world),BukkitAdapter.asBlockVector(first), BukkitAdapter.asBlockVector(second))
}
fun CuboidRegion.toLocations(world: World): Pair<Location, Location> {
    return this.minimumPoint.toLocation(world) to this.maximumPoint.toLocation(world)
}