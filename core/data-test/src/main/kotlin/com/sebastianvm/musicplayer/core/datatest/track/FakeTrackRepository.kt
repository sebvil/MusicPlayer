package com.sebastianvm.musicplayer.core.datatest.track

import com.sebastianvm.musicplayer.core.data.track.TrackRepository
import com.sebastianvm.musicplayer.core.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.core.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.core.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.core.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.GenreEntity
import com.sebastianvm.musicplayer.core.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.TrackEntity
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.Track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class FakeTrackRepository : TrackRepository {

    val tracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())

    private val genreTrackCrossRefs: MutableStateFlow<List<GenreTrackCrossRef>> =
        MutableStateFlow(emptyList())
    private val playlistTrackCrossRefs: MutableStateFlow<List<PlaylistTrackCrossRef>> =
        MutableStateFlow(emptyList())

    override fun getTrack(trackId: Long): Flow<Track> {
        return tracks.map { tracks -> tracks.first { it.id == trackId } }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTracksForMedia(mediaGroup: MediaGroup): Flow<List<Track>> {
        return when (mediaGroup) {
            is MediaGroup.AllTracks -> tracks
            is MediaGroup.Genre ->
                genreTrackCrossRefs.flatMapLatest { crossRefs ->
                    tracks.map { tracks ->
                        crossRefs
                            .filter { it.genreId == mediaGroup.genreId }
                            .map { crossRef -> tracks.first { it.id == crossRef.trackId } }
                    }
                }
            is MediaGroup.Playlist ->
                playlistTrackCrossRefs.flatMapLatest { crossRefs ->
                    tracks.map { tracks ->
                        crossRefs
                            .filter { it.playlistId == mediaGroup.playlistId }
                            .map { crossRef -> tracks.first { it.id == crossRef.trackId } }
                    }
                }
            is MediaGroup.Album ->
                tracks.map { tracks ->
                    tracks.filter { track -> track.albumId == mediaGroup.albumId }
                }
            is MediaGroup.Artist ->
                tracks.map { tracks ->
                    tracks.filter { track -> track.artists.any { it.id == mediaGroup.artistId } }
                }
            is MediaGroup.SingleTrack -> tracks
        }
    }

    override suspend fun insertAllTracks(
        tracks: Set<TrackEntity>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<ArtistEntity>,
        genres: Set<GenreEntity>,
        albums: Set<com.sebastianvm.musicplayer.core.database.entities.AlbumEntity>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>,
    ) {
        TODO("Not yet implemented")
    }
}
