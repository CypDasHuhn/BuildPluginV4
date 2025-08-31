package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.commands.wrapper.listEntryArgument
import dev.cypdashuhn.build.db.DbBuildsManager

fun buildNameArgument() = listEntryArgument("build", DbBuildsManager.all()) { it.name }