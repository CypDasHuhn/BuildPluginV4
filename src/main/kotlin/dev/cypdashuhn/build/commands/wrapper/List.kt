package dev.cypdashuhn.build.commands.wrapper

import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.TextArgument

fun <T> stringListArgument(
    key: String,
    list: List<String>,
    byName: (String) -> T?
): Argument<T> {
    return CustomArgument<T, String>(TextArgument(key)) { info ->
        val res = byName(info.input) ?: throw error("Invalid name: ", true)
        res
    }.replaceSuggestions(ArgumentSuggestions.strings(list))
}

fun genericListEntryArgument(
    key: String,
    list: List<String>
) = stringListArgument(key, list, byName = { input: String -> list.find { it == input } })

fun <T> listEntryArgument(
    key: String,
    list: List<T>,
    selector: (T) -> String
) = stringListArgument(key, list.map(selector), byName = { input: String -> list.find { selector(it) == input } })
