package dev.cypdashuhn.build.commands.wrapper

import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.TextArgument

fun nameArgument(
    key: String,
    isUnique: (String) -> Boolean
): Argument<String> {
    return CustomArgument(TextArgument(key)) { info ->
        if (!isUnique(info.input)) throw error("Name already used: ", true)
        if (info.input == "[Name]") throw error("Name cannot be '[Name]', this is a placeholder.", true)
        info.input
    }.simpleSuggestions("[Name]")
}
