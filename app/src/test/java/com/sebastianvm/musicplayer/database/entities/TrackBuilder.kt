package com.sebastianvm.musicplayer.database.entities

class TrackBuilder {

    private var trackId = DEFAULT_TRACK_ID
    private var trackName = DEFAULT_TRACK_NAME
    private var trackNumber = DEFAULT_TRACK_NUMBER
    private var trackDurationMs = DEFAULT_TRACK_DURATION
    private var albumId = DEFAULT_ALBUM_ID

    fun withTrackId(trackId: String) = apply { this.trackId = trackId }

    fun withTrackName(trackName: String) = apply { this.trackName = trackName }

    fun withTrackNumber(trackNumber: Long) = apply { this.trackNumber = trackNumber }

    fun withTrackDurationMs(trackDurationMs: Long) =
        apply { this.trackDurationMs = trackDurationMs }

    fun withAlbumId(albumId: String) = apply { this.albumId = albumId }

    fun build() = Track(
        trackId = trackId,
        trackName = trackName,
        trackNumber = trackNumber,
        trackDurationMs = trackDurationMs,
        albumId = albumId
    )

    companion object {
        const val DEFAULT_TRACK_ID = "1"
        const val DEFAULT_TRACK_NAME = "DEFAULT_TRACK_NAME"
        const val DEFAULT_TRACK_NUMBER = 1L
        const val DEFAULT_TRACK_DURATION = 120000L
        const val DEFAULT_ALBUM_ID = AlbumBuilder.PRIMARY_ALBUM_ID

        const val SECONDARY_TRACK_ID = "2"
        const val SECONDARY_TRACK_NAME = "TRACK_NAME_2"
        const val SECONDARY_TRACK_NUMBER = 2L
        const val SECONDARY_TRACK_DURATION = 180000L
        const val SECONDARY_ALBUM_ID = AlbumBuilder.SECONDARY_ALBUM_ID

        fun getDefaultTrack() = TrackBuilder()

        fun getSecondaryTrack() = TrackBuilder()
            .withTrackId(SECONDARY_TRACK_ID)
            .withTrackName(SECONDARY_TRACK_NAME)
            .withTrackNumber(SECONDARY_TRACK_NUMBER)
            .withTrackDurationMs(SECONDARY_TRACK_DURATION)
            .withAlbumId(SECONDARY_ALBUM_ID)

    }
}
