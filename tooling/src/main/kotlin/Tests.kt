import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors

class Tests : CliktCommand() {

    override fun run() {
        echo(TextColors.cyan("🧪 Running tests"))
        exec(DEBUG_TESTS)
    }

    companion object {
        private const val DEBUG_TESTS = "./gradlew testDebugUnitTest"
    }
}
