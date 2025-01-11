package de.cypdashuhn.rooster.commands.constructors

import de.cypdashuhn.build.BuildPlugin
import de.cypdashuhn.rooster.commands.*
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.entity.Player

object LocalizationArgument {
    fun full(
        literalName: String = t("rooster.language.label"),
        onChange: (InvokeInfo) -> Unit = { it.sender.tSend("rooster.language.changed") },
        argKey: String = "language",
        onInvalidLanguage: (ArgumentInfo, String) -> Unit = playerMessageExtra("rooster.language.invalid", argKey),
        onMissingLanguage: (ArgumentInfo) -> Unit = playerMessage("rooster.language.invalid")
    ): Argument {
        return Arguments.literal.single(literalName).followedBy(languageChanger(
            onChange, argKey, onInvalidLanguage, onMissingLanguage
        ))
    }
    fun languageChanger(
        onChange: (InvokeInfo) -> Unit = { it.sender.tSend("rooster.language.changed") },
        argKey: String = "language",
        onInvalidLanguage: (ArgumentInfo, String) -> Unit = playerMessageExtra("rooster.language.invalid", argKey),
        onMissingLanguage: (ArgumentInfo) -> Unit = playerMessage("rooster.language.invalid")
    ): Argument {
        return Arguments.list.single(
            key = "language",
            isEnabled = { it.sender is Player },
            list = BuildPlugin.getLocaleProvider().getLanguageCodes(),
            notMatchingError = onInvalidLanguage,
            onMissing = onMissingLanguage
        ).onExecute {
            val arg = it.context["language"] as String
            BuildPlugin.getLocaleProvider().changeLanguage(it as Player, arg)
            onChange(it)
        }
    }
}