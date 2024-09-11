import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File

class DeleteFiles : CliktCommand() {
    private val pattern: String by option().required()

    override fun run() {
        val regex = Regex(pattern)
        val directory = File("./")
        val filesToDelete =
            directory.walkTopDown().filter { file -> file.isFile && regex.matches(file.name) }

        val confirmation =
            terminal.prompt(
                """${filesToDelete.joinToString("\n")}
                Delete files?
            """
                    .trimIndent(),
                default = "n",
                choices = listOf("y", "n"),
            )

        if (confirmation == "y") {
            filesToDelete.forEach { it.delete() }
        }
    }
}
