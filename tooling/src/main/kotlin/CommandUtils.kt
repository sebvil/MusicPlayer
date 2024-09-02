import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

fun CliktCommand.exec(command: String, workingDir: File = File("./")) {
    val process =
        ProcessBuilder(*command.split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
    process.waitFor(60, TimeUnit.MINUTES)
    if (process.exitValue() != 0) {

        val errorStyle = TextColors.red + TextStyles.bold

        echo(errorStyle("Command \"$command\" failed"), err = true)
        exitProcess(process.exitValue())
    }
}

fun CliktCommand.exec(vararg commands: String, workingDir: File = File("./")) {
    commands.forEach { command -> exec(command = command, workingDir = workingDir) }
}
