package com.sebastianvm.musicplayer.database.entities

@DslMarker
annotation class AlbumDsl

@AlbumDsl
class AlbumBuilder {
    var albumId = 0L
    var albumName = ""
    var year = 0L
    var artists = ""

    fun build(): Album {
        return Album(
            id = albumId,
            albumName = albumName,
            year = year,
            artists = artists
        )
    }

    companion object {
        fun getDefaultInstance() = AlbumBuilder().build()
    }
}

@AlbumDsl
class FullAlbumInfoBuilder {
    var album  = AlbumBuilder.getDefaultInstance()
    var artistIds: MutableList<Long> = mutableListOf()
    private var trackIds: MutableList<Long> = mutableListOf()

    fun album(init: AlbumBuilder.() -> Unit): Album {
        val builder = AlbumBuilder()
        builder.init()
        album = builder.build()
        return album
    }

    fun artistIds(init: MutableList<Long>.() -> Unit) : MutableList<Long> {
        artistIds.init()
        return artistIds
    }

    fun trackIds(init: MutableList<Long>.() -> Unit) : MutableList<Long> {
        trackIds.init()
        return trackIds
    }

    fun build(): FullAlbumInfo {
        return FullAlbumInfo(
            album = album,
            artists = artistIds,
            tracks = trackIds
        )
    }
}

fun fullAlbumInfo(init: FullAlbumInfoBuilder.() -> Unit): FullAlbumInfo {
    val builder = FullAlbumInfoBuilder()
    builder.init()
    return builder.build()
}