import com.github.ajalt.clikt.core.CliktCommand

class Checks : CliktCommand() {
    override fun run() {
        SortLibs().parse(emptyList())
        Format().parse(emptyList())
        Tests().parse(emptyList())
        Lint().parse(emptyList())
    }
}
