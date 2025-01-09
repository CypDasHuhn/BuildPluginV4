import de.cypdashuhn.build.commands.build.BuildCommand

fun main() {
    BuildCommand.command.displayPaths().forEach {
        println(it)
    }
}