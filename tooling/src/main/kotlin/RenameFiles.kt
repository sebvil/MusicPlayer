import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File

class RenameFiles : CliktCommand() {
    private val pattern: String by option().required()
    private val replacement: String by option().required()
    private val dryRun: Boolean by option().flag("--execute", default = false)

    override fun run() {
        val regex = Regex(pattern)
        val directory = File("./")
        directory.walkTopDown().forEach { file ->
            if (file.isFile && regex.matches(file.name)) {
                val newFileName = regex.replace(input = file.name, replacement = replacement)
                if (dryRun) {
                    echo("Renaming ${file.name} to $newFileName")
                } else {
                    val newFile = File(file.parentFile, newFileName)
                    file.renameTo(newFile)
                }
            }
        }
    }
}
