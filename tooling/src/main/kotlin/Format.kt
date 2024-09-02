import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors

class Format : CliktCommand() {

    override fun run() {
        echo(TextColors.cyan("🧹️ Formatting code"))
        exec(BUILD_LOGIC_KTFMT_FORMAT, KTFMT_FORMAT)
    }

    companion object {
        private const val KTFMT_FORMAT = "./gradlew ktfmtFormat"
        private const val BUILD_LOGIC_KTFMT_FORMAT = "./gradlew :build-logic:convention:ktfmtFormat"
    }
}
