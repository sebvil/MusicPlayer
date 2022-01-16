package com.sebastianvm.musicplayer.database.entities

class AlbumBuilder {
    private var albumId = PRIMARY_ALBUM_ID
    private var albumName = PRIMARY_ALBUM_NAME
    private var year = PRIMARY_YEAR
    private var numberOfTracks = PRIMARY_NUMBER_OF_TRACKS

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
        const val PRIMARY_ALBUM_ID = "1"
        const val PRIMARY_ALBUM_NAME = "PRIMARY_ALBUM_NAME"
        const val PRIMARY_YEAR = 2022L
        const val PRIMARY_NUMBER_OF_TRACKS = 10L

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
