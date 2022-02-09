package com.sebastianvm.musicplayer.database.entities


@DslMarker
annotation class GenreDsl

@GenreDsl
class GenreBuilder {
    var genreName = ""

    fun build(): Genre {
        return Genre(genreName = genreName)
    }
}

fun genre(init: GenreBuilder.() -> Unit): Genre {
    val builder = GenreBuilder()
    builder.init()
    return builder.build()
}