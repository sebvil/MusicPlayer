package com.sebastianvm.musicplayer.database.entities

class GenreBuilder {

    private var genreName = PRIMARY_GENRE_NAME

    fun withGenreName(genreName: String) = apply {
        this.genreName = genreName
    }

    fun build() = Genre(genreName)

    companion object {
        const val PRIMARY_GENRE_NAME = "PRIMARY_GENRE_NAME"
        const val SECONDARY_GENRE_NAME = "SECONDARY_GENRE_NAME"

        fun getDefaultGenre() = GenreBuilder()
        fun getSecondaryGenre() = GenreBuilder().withGenreName(SECONDARY_GENRE_NAME)
    }
}
