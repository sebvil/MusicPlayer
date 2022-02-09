package com.sebastianvm.musicplayer.database.entities

@DslMarker
annotation class TrackDsl

@TrackDsl
class TrackBuilder {
    var trackId = "0"
    var trackName = ""
    var trackNumber = 0L
    var trackDurationMs = 0L
    var albumName = ""
    var albumId = "0"
    var artists = ""

    fun build(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            trackNumber = trackNumber,
            trackDurationMs = trackDurationMs,
            albumName = albumName,
            albumId = albumId,
            artists = artists
        )
    }

    companion object {
        fun getDefaultInstance() = TrackBuilder().build()
    }
}

@TrackDsl
class FullTrackInfoBuilder {
    private var track = TrackBuilder.getDefaultInstance()
    private var artistIds: MutableList<String> = mutableListOf()
    private var genreIds: MutableList<String> = mutableListOf()

    fun track(init: TrackBuilder.() -> Unit): Track {
        val builder = TrackBuilder()
        builder.init()
        track = builder.build()
        return track
    }

    fun artistIds(init: MutableList<String>.() -> Unit): MutableList<String> {
        artistIds.init()
        return artistIds
    }

    fun genreIds(init: MutableList<String>.() -> Unit): MutableList<String> {
        genreIds.init()
        return genreIds
    }

    fun build(): FullTrackInfo {
        return FullTrackInfo(
            track = track,
            artists = artistIds,
            genres = genreIds
        )
    }
}

fun fullTrackInfo(init: FullTrackInfoBuilder.() -> Unit): FullTrackInfo {
    val builder = FullTrackInfoBuilder()
    builder.init()
    return builder.build()
}