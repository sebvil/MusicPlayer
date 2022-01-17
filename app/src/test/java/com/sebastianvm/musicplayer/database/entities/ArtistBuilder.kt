package com.sebastianvm.musicplayer.database.entities

class ArtistBuilder {

    private var artistName = DEFAULT_ARTIST_NAME

    fun withArtistName(artistName: String) = apply {
        this.artistName = artistName
    }

    fun build() = Artist(artistName)

    companion object {
        const val DEFAULT_ARTIST_NAME = "DEFAULT_ARTIST_NAME"
        const val SECONDARY_ARTIST_NAME = "SECONDARY_ARTIST_NAME"

        fun getDefaultArtist() = ArtistBuilder()
        fun getSecondaryArtist() = ArtistBuilder().withArtistName(SECONDARY_ARTIST_NAME)
    }
}
