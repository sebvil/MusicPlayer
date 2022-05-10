package com.sebastianvm.musicplayer.database.entities

@DslMarker
annotation class ArtistDsl

@ArtistDsl
class ArtistBuilder {
    var artistName = ""

    fun build(): Artist {
        return Artist(artistName = artistName)
    }

    companion object {
        fun getDefaultInstance() = ArtistBuilder().build()
    }
}

@ArtistDsl
class ArtistWithAlbumsBuilder {
    private var artist = ArtistBuilder.getDefaultInstance()
    private var albumsForArtistIds: MutableList<Long> = mutableListOf()
    private var appearsOnForArtistIds: MutableList<Long> = mutableListOf()

    fun artist(init: ArtistBuilder.() -> Unit): Artist {
        val builder = ArtistBuilder()
        builder.init()
        artist = builder.build()
        return artist
    }

    fun albumsForArtistIds(init: MutableList<Long>.() -> Unit): MutableList<Long> {
        albumsForArtistIds.init()
        return albumsForArtistIds
    }

    fun appearsOnForArtistIds(init: MutableList<Long>.() -> Unit): MutableList<Long> {
        appearsOnForArtistIds.init()
        return appearsOnForArtistIds
    }

    fun build(): ArtistWithAlbums {
        return ArtistWithAlbums(
            artist = artist,
            artistAlbums = albumsForArtistIds,
            artistAppearsOn = appearsOnForArtistIds
        )
    }
}

fun artistWithAlbums(init: ArtistWithAlbumsBuilder.() -> Unit): ArtistWithAlbums {
    val builder = ArtistWithAlbumsBuilder()
    builder.init()
    return builder.build()
}