package dev.cypdashuhn.build.commands.build

import dev.cypdashuhn.build.commands.wrapper.listArgument
import dev.cypdashuhn.build.db.DbBuildsManager
import dev.jorel.commandapi.StringTooltip

fun buildNameArgument() = listArgument("build", { DbBuildsManager.all() }, { name }) {
    StringTooltip.ofString(
        name,
        "$readableDimensions | frames: $frameAmount"
    )
}