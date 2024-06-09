package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.musicplayer.database.entities.AlbumEntity
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.GenreEntity
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.TrackEntity
import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTrackRepository : TrackRepository {

    val trackListsWithMetadata: MutableStateFlow<Map<TrackList, TrackListWithMetadata>> =
        MutableStateFlow(emptyMap())

    override fun getTrack(trackId: Long): Flow<Track> {
        TODO("Not yet implemented")
    }

    override fun getTracksForMedia(mediaGroup: MediaGroup): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun getTrackListWithMetaData(trackList: TrackList): Flow<TrackListWithMetadata> {
        return trackListsWithMetadata.map { it[trackList] ?: error("Track list not found") }
    }

    override suspend fun insertAllTracks(
        tracks: Set<TrackEntity>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<ArtistEntity>,
        genres: Set<GenreEntity>,
        albums: Set<AlbumEntity>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>,
    ) {
        TODO("Not yet implemented")
    }
}
