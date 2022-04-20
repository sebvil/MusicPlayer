package com.sebastianvm.musicplayer.database.entities

import com.sebastianvm.musicplayer.util.Fixtures


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

fun genreFixture(): Genre = genre {
    genreName = Fixtures.getRandomString()
}

fun genreFixtureList(numItems: Int): List<Genre> {
    return List(numItems) {
        genreFixture()
    }
}

