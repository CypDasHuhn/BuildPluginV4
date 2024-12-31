package de.cypdashuhn.rooster.commands_new

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object Command : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val result = ArgumentParser.parse(sender, label, args, ArgumentParser.CommandParseType.Invocation)

        if (result.success) result.invocationLambda.invoke()
        else result.errorInvocation.invoke()

        return true
    }
}