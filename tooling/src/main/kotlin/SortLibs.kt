import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors
import java.io.File

class SortLibs : CliktCommand() {

    override fun run() {
        echo(TextColors.cyan("↕️ Sorting libs"))
        val path = "./gradle/libs.versions.toml"
        val file = File(path)
        val libs = file.readLines()
        val sortedLibs = buildList {
            var unsortedLibs = libs
            while (unsortedLibs.isNotEmpty()) {
                val sortBeginIndex = libs.indexOf("# sort")
                if (sortBeginIndex == -1) {
                    addAll(unsortedLibs)
                    return@buildList
                }
                addAll(unsortedLibs.take(sortBeginIndex + 1))
                unsortedLibs = unsortedLibs.drop(sortBeginIndex + 1)
                val sortEnd = unsortedLibs.indexOf("# endsort")
                if (sortEnd == -1) {
                    addAll(unsortedLibs)
                    return@buildList
                }
                addAll(unsortedLibs.take(sortEnd).sorted())
                add("# endsort")
                unsortedLibs = unsortedLibs.drop(sortEnd + 1)
            }
        }
        file.writeText(sortedLibs.joinToString("\n"))
    }
}
