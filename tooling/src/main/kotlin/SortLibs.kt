import com.github.ajalt.clikt.core.CliktCommand
import java.io.File

class SortLibs : CliktCommand() {

    override fun run() {
        val path = "./gradle/libs.versions.toml"
        println("Sorting libs")
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
