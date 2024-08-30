import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) = Main().subcommands(SortLibs()).main(args)

class Main : CliktCommand() {

    override fun run() = Unit
}
