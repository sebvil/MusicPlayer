import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors

class Lint : CliktCommand() {

    override fun run() {
        echo(TextColors.cyan("🔍 Running linters"))
        exec(DETEKT_BUILD_LOGIC, DETEKT_MAIN, ANDROID_LINT)
    }

    companion object {
        private const val DETEKT_BUILD_LOGIC = "./gradlew :build-logic:convention:detektMain"
        private const val DETEKT_MAIN = "./gradlew detektMain"
        private const val ANDROID_LINT = "./gradlew lint"
    }
}
