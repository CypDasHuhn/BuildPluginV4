package de.cypdashuhn.rooster.simulator.commands

import de.cypdashuhn.rooster.commands.parsing.ArgumentParser
import de.cypdashuhn.rooster.commands.parsing.Completer.withStarting
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.simulator.Simulator

object CommandSimulatorHandler {
    fun invokeCommand(command: String): InvokeResult {
        val result = command(command, ArgumentParser.CommandParseType.Invocation)
        return InvokeResult(result.success, if (result.success) result.invocationLambda else result.errorInvocation)
    }

    fun invokeCommandTerminal(command: String) {
        val result = invokeCommand(command)

        if (result.success) {
            result.invocationLambda()
            Simulator.printValues()
        } else {
            println("An error occurred!")
            result.invocationLambda()
            println("# ${Simulator.error}")
        }
    }

    fun completeCommand(command: String): CompleteResult {
        val result = command(command, ArgumentParser.CommandParseType.TabCompleter)
        return CompleteResult(result.success, result.tabCompleterList)
    }

    fun completeCommandTerminal(command: String) {
        val result = completeCommand(command)

        println("Success: ${result.success}")
        if (result.success) {
            val lastArg = command.split(" ").last()
            println("Completions: ")
            result.tabCompleterList.withStarting(lastArg).forEach {
                println("# $it")
            }
        }
    }

    fun command(command: String, type: ArgumentParser.CommandParseType): ArgumentParser.ReturnResult {
        val (label, args) = commandTokenized(command)

        if (!Rooster.registeredCommands.any { it.labels.any { it.lowercase() == label.lowercase() } }) {
            println("Unknown command: $command")
            return ArgumentParser.ReturnResult()
        }

        requireNotNull(Simulator.player) { "Simulator not initialized" }

        return ArgumentParser.parse(Simulator.player!!, label, args, type)
    }

    fun commandTokenized(command: String): Pair<String, Array<String>> {
        var command = command
        if (command.startsWith("/")) {
            command = command.substring(1)
        }
        val parts = command.split(" ")
        val label = parts.firstOrNull() ?: ""
        val args = parts.drop(1).toMutableList()

        return label to args.toTypedArray()
    }
}