package dev.cypdashuhn.build.commands.settings

import dev.jorel.commandapi.CommandAPICommand

val settings = CommandAPICommand("!settings")
    .withArguments()
    .register()