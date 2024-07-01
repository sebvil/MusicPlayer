package com.sebastianvm.musicplayer.core.data.track

import com.sebastianvm.musicplayer.core.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.core.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.core.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.core.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.GenreEntity
import com.sebastianvm.musicplayer.core.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.TrackEntity
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun getTrack(trackId: Long): Flow<Track>

    fun getTracksForMedia(mediaGroup: MediaGroup): Flow<List<Track>>

    suspend fun insertAllTracks(
        tracks: Set<TrackEntity>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<ArtistEntity>,
        genres: Set<GenreEntity>,
        albums: Set<com.sebastianvm.musicplayer.core.database.entities.AlbumEntity>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>,
    )
}
