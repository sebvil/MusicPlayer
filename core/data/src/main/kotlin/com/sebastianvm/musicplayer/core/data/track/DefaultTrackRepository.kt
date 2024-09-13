package com.sebastianvm.musicplayer.core.data.track

import com.sebastianvm.musicplayer.core.data.artist.asExternalModel
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.database.daos.TrackDao
import com.sebastianvm.musicplayer.core.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.core.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.core.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.core.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.BasicTrackEntity
import com.sebastianvm.musicplayer.core.database.entities.DetailedTrack
import com.sebastianvm.musicplayer.core.database.entities.GenreEntity
import com.sebastianvm.musicplayer.core.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.TrackEntity
import com.sebastianvm.musicplayer.core.model.BasicTrack
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.Track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class DefaultTrackRepository(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val trackDao: TrackDao,
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
                    sortOption = mediaSortPreferences.sortOption.name,
                    sortOrder = mediaSortPreferences.sortOrder.name,
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
                    sortOption = mediaSortPreferences.sortOption.name,
                    sortOrder = mediaSortPreferences.sortOrder.name,
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
                    sortOption = sortPreferences.sortOption.name,
                    sortOrder = sortPreferences.sortOrder.name,
                )
            }
            .distinctUntilChanged()
    }

    // endregion
}

private fun Flow<List<DetailedTrack>>.asExternalModelFlow(): Flow<List<Track>> {
    return map { tracks -> tracks.map { it.asExternalModel() } }
}

fun BasicTrackEntity.asExternalModel() = BasicTrack(id, trackName, artists)

fun DetailedTrack.asExternalModel() =
    Track(
        id = track.id,
        name = track.trackName,
        artists = artists.map { it.asExternalModel() },
        albumId = album.id,
    )
