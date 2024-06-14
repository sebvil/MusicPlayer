package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.entities.AlbumEntity
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.DetailedTrack
import com.sebastianvm.musicplayer.database.entities.GenreEntity
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.TrackEntity
import com.sebastianvm.musicplayer.database.entities.asExternalModel
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.model.TrackListMetadata
import com.sebastianvm.musicplayer.model.TrackListWithMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class TrackRepositoryImpl(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val trackDao: TrackDao,
    private val playlistRepository: PlaylistRepository,
    private val genreRepository: GenreRepository,
    private val albumRepository: AlbumRepository,
) : TrackRepository {

    override fun getTrack(trackId: Long): Flow<Track> {
        return trackDao.getTrack(trackId).map { it.asExternalModel() }.distinctUntilChanged()
    }

    override fun getTracksForMedia(mediaGroup: MediaGroup): Flow<List<Track>> {
        return when (mediaGroup) {
            is MediaGroup.AllTracks -> getAllTracks().asExternalModelFlow()
            is MediaGroup.Genre -> getTracksForGenre(mediaGroup.genreId).asExternalModelFlow()
            is MediaGroup.Playlist ->
                getTracksForPlaylist(mediaGroup.playlistId).asExternalModelFlow()
            is MediaGroup.Album -> getTracksForAlbum(mediaGroup.albumId).asExternalModelFlow()
            is MediaGroup.Artist -> getTracksForArtist(mediaGroup.artistId).asExternalModelFlow()
            is MediaGroup.SingleTrack -> getTrack(mediaGroup.trackId).map { listOf(it) }
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
        trackDao.insertAllTracks(
            tracks = tracks,
            artistTrackCrossRefs = artistTrackCrossRefs,
            genreTrackCrossRefs = genreTrackCrossRefs,
            artists = artists,
            genres = genres,
            albums = albums,
            albumsForArtists = albumsForArtists,
            appearsOnForArtists = appearsOnForArtists,
        )
    }

    // region Private helpers
    private fun getAllTracks(): Flow<List<DetailedTrack>> {
        return sortPreferencesRepository
            .getTrackListSortPreferences(trackList = MediaGroup.AllTracks)
            .flatMapLatest { mediaSortPreferences ->
                trackDao.getAllTracks(
                    sortOption = mediaSortPreferences.sortOption,
                    sortOrder = mediaSortPreferences.sortOrder,
                )
            }
            .distinctUntilChanged()
    }

    private fun getTracksForArtist(artistId: Long): Flow<List<DetailedTrack>> {
        return trackDao.getTracksForArtist(artistId).distinctUntilChanged()
    }

    private fun getTracksForAlbum(albumId: Long): Flow<List<DetailedTrack>> {
        return trackDao.getTracksForAlbum(albumId).distinctUntilChanged()
    }

    private fun getTracksForGenre(genreId: Long): Flow<List<DetailedTrack>> {
        return sortPreferencesRepository
            .getTrackListSortPreferences(trackList = MediaGroup.Genre(genreId = genreId))
            .flatMapLatest { mediaSortPreferences ->
                trackDao.getTracksForGenre(
                    genreId = genreId,
                    sortOption = mediaSortPreferences.sortOption,
                    sortOrder = mediaSortPreferences.sortOrder,
                )
            }
            .distinctUntilChanged()
    }

    private fun getTracksForPlaylist(playlistId: Long): Flow<List<DetailedTrack>> {
        return sortPreferencesRepository
            .getPlaylistSortPreferences(playlistId = playlistId)
            .flatMapLatest { sortPreferences ->
                trackDao.getTracksForPlaylist(
                    playlistId = playlistId,
                    sortOption = sortPreferences.sortOption,
                    sortOrder = sortPreferences.sortOrder,
                )
            }
            .distinctUntilChanged()
    }

    private fun getTrackListMetadata(trackList: TrackList): Flow<TrackListMetadata?> {
        return when (trackList) {
            is MediaGroup.AllTracks -> flowOf(null)
            is MediaGroup.Genre ->
                genreRepository.getGenreName(trackList.genreId).map {
                    TrackListMetadata(trackListName = it)
                }
            is MediaGroup.Playlist ->
                playlistRepository.getPlaylistName(trackList.playlistId).map {
                    TrackListMetadata(trackListName = it)
                }
            is MediaGroup.Album ->
                albumRepository.getBasicAlbum(trackList.albumId).map {
                    TrackListMetadata(
                        trackListName = it.title,
                        mediaArtImageState =
                            MediaArtImageState(it.imageUri, backupImage = Icons.Album),
                    )
                }
        }
    }

    // endregion
}

private fun Flow<List<DetailedTrack>>.asExternalModelFlow(): Flow<List<Track>> {
    return map { tracks -> tracks.map { it.asExternalModel() } }
}
