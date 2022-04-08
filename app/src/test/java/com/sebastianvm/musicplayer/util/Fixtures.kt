package com.sebastianvm.musicplayer.util

object Fixtures {
    fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + ' '
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
