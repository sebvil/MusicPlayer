package com.sebastianvm.musicplayer.database.entities

class AlbumBuilder {
    private var albumId = DEFAULT_ALBUM_ID
    private var albumName = DEFAULT_ALBUM_NAME
    private var year = DEFAULT_YEAR
    private var numberOfTracks = DEFAULT_NUMBER_OF_TRACKS

    fun withAlbumId(albumId: String): AlbumBuilder = apply {
        this.albumId = albumId
    }

    fun withAlbumName(albumName: String) = apply {
        this.albumName = albumName
    }

    fun withYear(year: Long) = apply {
        this.year = year
    }

    fun withNumberOfTracks(numberOfTracks: Long) = apply {
        this.numberOfTracks = numberOfTracks
    }

    fun build(): Album = Album(
        albumId = albumId,
        albumName = albumName,
        year = year,
        numberOfTracks = numberOfTracks
    )



    companion object {
        const val DEFAULT_ALBUM_ID = "1"
        const val DEFAULT_ALBUM_NAME = "DEFAULT_ALBUM_NAME"
        const val DEFAULT_YEAR = 2022L
        const val DEFAULT_NUMBER_OF_TRACKS = 10L

        const val SECONDARY_ALBUM_ID = "2"
        const val SECONDARY_ALBUM_NAME = "SECONDARY_ALBUM_NAME"
        const val SECONDARY_YEAR = 2021L
        const val SECONDARY_NUMBER_OF_TRACKS = 5L

        fun getDefaultAlbum() = AlbumBuilder()

        fun getSecondaryAlbum() = AlbumBuilder()
            .withAlbumId(SECONDARY_ALBUM_ID)
            .withAlbumName(SECONDARY_ALBUM_NAME)
            .withYear(SECONDARY_YEAR)
            .withNumberOfTracks(SECONDARY_NUMBER_OF_TRACKS)
    }


}
