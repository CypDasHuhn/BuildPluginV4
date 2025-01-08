package de.cypdashuhn.rooster.commands.parsing

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object Completer : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        commands: Command,
        label: String,
        args: Array<String>
    ): List<String> {
        val result = ArgumentParser.parse(sender, label, args, ArgumentParser.CommandParseType.TabCompleter)
        return result.tabCompleterList.withStarting(args.last())
    }

    fun List<String>.withStarting(str: String, ignoreCase: Boolean = false): List<String> {
        return this.filter { it.startsWith(str, ignoreCase = ignoreCase) }
    }
}