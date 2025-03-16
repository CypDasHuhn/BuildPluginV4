import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.UnfinishedArgument

fun main() {
    val s = testCommand.command
}

object testCommand : RoosterCommand("!test") {
    override fun content(arg: UnfinishedArgument): Argument {
        return arg.onExecute { }
    }
}