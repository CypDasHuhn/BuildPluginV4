package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.entity.Player

object LocalizationArgument {
    fun full(
        literalName: String = t("rooster.language.label"),
        onChange: (InvokeInfo) -> Unit = { it.sender.tSend("rooster.language.changed") },
        argKey: String = "language",
        onInvalidLanguage: (ArgumentInfo, String) -> Unit = playerMessageExtra(
            "rooster.language.invalid_error",
            argKey
        ),
        onMissingLanguage: (ArgumentInfo) -> Unit = playerMessage("rooster.language.missing_error")
    ): Argument {
        return Arguments.literal.single(literalName).followedBy(
            languageChanger(
                onChange, argKey, onInvalidLanguage, onMissingLanguage
            )
        )
    }

    fun languageChanger(
        onChange: (InvokeInfo) -> Unit = { it.sender.tSend("rooster.language.changed") },
        argKey: String = "language",
        onInvalidLanguage: (ArgumentInfo, String) -> Unit = playerMessageExtra(
            "rooster.language.invalid_error",
            argKey
        ),
        onMissingLanguage: (ArgumentInfo) -> Unit = playerMessage("rooster.language.missing_error")
    ): Argument {
        return Arguments.list.single(
            key = "language",
            isEnabled = { it.sender is Player },
            list = Rooster.localeProvider.getLanguageCodes(),
            notMatchingError = onInvalidLanguage,
            onMissing = onMissingLanguage
        ).onExecute {
            val arg = it.context["language"] as String
            Rooster.localeProvider.changeLanguage(it as Player, arg)
            onChange(it)
        }
    }
}