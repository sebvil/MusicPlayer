package com.sebastianvm.musicplayer.util

object Fixtures {
    fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + ' '
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }

    inline fun <reified T: Enum<T>> getRandomEnum(validOptions: List<T> = listOf()): T {
        return (validOptions.ifEmpty { enumValues<T>().toList() }).random()
    }
}
