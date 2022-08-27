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
import com.sebastianvm.musicplayer.database.entities.TrackWithArtists
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class TrackRepositoryImpl @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val trackDao: TrackDao,
    private val playlistRepository: PlaylistRepository,
) : TrackRepository {

    override fun getTracksCount(): Flow<Int> {
        return trackDao.getTracksCount().distinctUntilChanged()
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return sortPreferencesRepository.getTrackListSortPreferences(trackListType = TrackListType.ALL_TRACKS)
            .flatMapLatest { mediaSortPreferences ->
                trackDao.getAllTracks(
                    sortOption = mediaSortPreferences.sortOption,
                    sortOrder = mediaSortPreferences.sortOrder
                )
            }.distinctUntilChanged()
    }

    override fun getTrack(trackId: Long): Flow<TrackWithArtists> {
        return trackDao.getTrack(trackId).distinctUntilChanged()
    }

    override fun getTracksForArtist(artistId: Long): Flow<List<Track>> {
        return trackDao.getTracksForArtist(artistId).distinctUntilChanged()
    }

    override fun getTracksForAlbum(albumId: Long): Flow<List<Track>> {
        return trackDao.getTracksForAlbum(albumId).distinctUntilChanged()
    }

    override fun getTracksForGenre(genreId: Long): Flow<List<Track>> {
        return sortPreferencesRepository.getTrackListSortPreferences(trackListType = TrackListType.GENRE)
            .flatMapLatest { mediaSortPreferences ->
                trackDao.getTracksForGenre(
                    genreId = genreId,
                    sortOption = mediaSortPreferences.sortOption,
                    sortOrder = mediaSortPreferences.sortOrder
                )
            }.distinctUntilChanged()
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return sortPreferencesRepository.getPlaylistSortPreferences(playlistId = playlistId)
            .flatMapLatest { sortPreferences ->
                trackDao.getTracksForPlaylist(
                    playlistId = playlistId,
                    sortOption = sortPreferences.sortOption,
                    sortOrder = sortPreferences.sortOrder
                )
            }.distinctUntilChanged()
    }

    override fun getTracksForMedia(
        trackListType: TrackListType,
        mediaId: Long
    ): Flow<List<ModelListItemState>> {
        return when (trackListType) {
            TrackListType.ALL_TRACKS -> getAllTracks()
                .map { tracks -> tracks.map { it.toModelListItemState() } }
            TrackListType.GENRE -> getTracksForGenre(mediaId)
                .map { tracks -> tracks.map { it.toModelListItemState() } }
            TrackListType.PLAYLIST -> playlistRepository.getTracksInPlaylist(mediaId)
                .map { tracks -> tracks.map { it.toModelListItemState() } }
            TrackListType.ALBUM -> getTracksForAlbum(mediaId)
                .map { tracks -> tracks.map { it.toModelListItemState() } }
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
        appearsOnForArtists: Set<AppearsOnForArtist>
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
