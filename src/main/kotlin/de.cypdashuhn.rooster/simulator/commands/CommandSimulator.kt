package de.cypdashuhn.rooster.simulator.commands

data class InvokeResult(val success: Boolean, val invocationLambda: () -> Unit)
data class CompleteResult(val success: Boolean, val tabCompleterList: List<String>)
object CommandSimulator {

    fun invokeCommand(command: String) {
        val result = CommandSimulatorHandler.invokeCommand(command)
        result.invocationLambda()
    }

    fun invokeCommandObject(command: String): InvokeResult {
        return CommandSimulatorHandler.invokeCommand(command)
    }

    fun completeCommand(command: String): CompleteResult {
        return CommandSimulatorHandler.completeCommand(command)
    }
}