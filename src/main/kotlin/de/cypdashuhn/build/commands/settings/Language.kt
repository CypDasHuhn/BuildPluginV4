package de.cypdashuhn.build.commands.settings

import de.cypdashuhn.rooster.commands.Arguments
import de.cypdashuhn.rooster.localization.t

val language = Arguments.literal.single(t("build.language.label"))
