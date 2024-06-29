package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.database.entities.AlbumEntity
import com.sebastianvm.database.entities.AlbumsForArtist
import com.sebastianvm.database.entities.AppearsOnForArtist
import com.sebastianvm.database.entities.ArtistEntity
import com.sebastianvm.database.entities.ArtistTrackCrossRef
import com.sebastianvm.database.entities.GenreEntity
import com.sebastianvm.database.entities.GenreTrackCrossRef
import com.sebastianvm.database.entities.TrackEntity
import com.sebastianvm.model.Track
import com.sebastianvm.musicplayer.player.MediaGroup
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
        albums: Set<AlbumEntity>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>,
    )
}
