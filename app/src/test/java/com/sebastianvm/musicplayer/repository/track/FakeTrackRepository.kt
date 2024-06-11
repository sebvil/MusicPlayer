package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.musicplayer.database.entities.AlbumEntity
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.GenreEntity
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.TrackEntity
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.model.Album
import com.sebastianvm.musicplayer.model.Genre
import com.sebastianvm.musicplayer.model.Playlist
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.model.TrackListMetadata
import com.sebastianvm.musicplayer.model.TrackListWithMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FakeTrackRepository : TrackRepository {

    val tracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
    val albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val genres: MutableStateFlow<List<Genre>> = MutableStateFlow(emptyList())
    val playlists: MutableStateFlow<List<Playlist>> = MutableStateFlow(emptyList())

    val genreTrackCrossRefs: MutableStateFlow<List<GenreTrackCrossRef>> =
        MutableStateFlow(emptyList())
    val playlistTrackCrossRefs: MutableStateFlow<List<PlaylistTrackCrossRef>> =
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

    override fun getTrackListWithMetaData(trackList: TrackList): Flow<TrackListWithMetadata> {
        return combine(
            getTrackListMetadata(trackList),
            getTracksForMedia(mediaGroup = trackList),
        ) { metadata, tracks ->
            TrackListWithMetadata(metadata, tracks)
        }
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

    private fun getTrackListMetadata(trackList: TrackList): Flow<TrackListMetadata?> {
        return when (trackList) {
            is MediaGroup.AllTracks -> flowOf(null)
            is MediaGroup.Genre ->
                genres
                    .map { genres -> genres.first { it.id == trackList.genreId } }
                    .map { TrackListMetadata(trackListName = it.name) }
            is MediaGroup.Playlist ->
                playlists
                    .map { playlists -> playlists.first { it.id == trackList.playlistId } }
                    .map { TrackListMetadata(trackListName = it.name) }
            is MediaGroup.Album ->
                albums
                    .map { albums -> albums.first { it.id == trackList.albumId } }
                    .map {
                        TrackListMetadata(
                            trackListName = it.title,
                            mediaArtImageState =
                                MediaArtImageState(
                                    imageUri = it.imageUri,
                                    backupImage = Icons.Album
                                )
                        )
                    }
        }
    }
}
