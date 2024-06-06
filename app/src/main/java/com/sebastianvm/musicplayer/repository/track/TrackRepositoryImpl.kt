package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.database.entities.TrackWithArtists
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class TrackRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val trackDao: TrackDao,
    private val playlistRepository: PlaylistRepository,
    private val genreRepository: GenreRepository,
    private val albumRepository: AlbumRepository,
) : TrackRepository {

    private fun getAllTracks(): Flow<List<Track>> {
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

    override fun getTrack(trackId: Long): Flow<TrackWithArtists> {
        return trackDao.getTrack(trackId).distinctUntilChanged()
    }

    private fun getTracksForArtist(artistId: Long): Flow<List<Track>> {
        return trackDao.getTracksForArtist(artistId).distinctUntilChanged()
    }

    private fun getTracksForAlbum(albumId: Long): Flow<List<Track>> {
        return trackDao.getTracksForAlbum(albumId).distinctUntilChanged()
    }

    private fun getTracksForGenre(genreId: Long): Flow<List<Track>> {
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

    private fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
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

    override fun getTracksForMedia(mediaGroup: MediaGroup): Flow<List<Track>> {
        return when (mediaGroup) {
            is MediaGroup.AllTracks -> getAllTracks()
            is MediaGroup.Genre -> getTracksForGenre(mediaGroup.genreId)
            is MediaGroup.Playlist -> getTracksForPlaylist(mediaGroup.playlistId)
            is MediaGroup.Album -> getTracksForAlbum(mediaGroup.albumId)
            is MediaGroup.Artist -> getTracksForArtist(mediaGroup.artistId)
            is MediaGroup.SingleTrack -> getTrack(mediaGroup.trackId).map { listOf(it.track) }
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
                albumRepository.getAlbum(trackList.albumId).map {
                    TrackListMetadata(
                        trackListName = it.albumName,
                        mediaArtImageState =
                            MediaArtImageState(it.imageUri, backupImage = Icons.Album),
                    )
                }
        }
    }

    override suspend fun insertAllTracks(
        tracks: Set<Track>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<Artist>,
        genres: Set<Genre>,
        albums: Set<Album>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>,
    ) {
        withContext(ioDispatcher) {
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
    }
}
