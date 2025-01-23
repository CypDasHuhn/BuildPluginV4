package de.cypdashuhn.rooster.localization

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage

fun minimessage(text: String) = MiniMessage.miniMessage().deserialize(text) as TextComponent
fun minimessage(text: TextComponent) = minimessage(text.content())
