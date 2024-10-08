package com.sebastianvm.musicplayer.core.data.playlist

import com.sebastianvm.musicplayer.core.common.extensions.mapValues
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.data.track.asExternalModel
import com.sebastianvm.musicplayer.core.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.core.database.entities.PlaylistEntity
import com.sebastianvm.musicplayer.core.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.PlaylistWithTracksEntity
import com.sebastianvm.musicplayer.core.model.BasicPlaylist
import com.sebastianvm.musicplayer.core.model.Playlist
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@OptIn(ExperimentalCoroutinesApi::class)
@Factory
class DefaultPlaylistRepository(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val playlistDao: PlaylistDao,
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<BasicPlaylist>> {
        return sortPreferencesRepository
            .getPlaylistsListSortOrder()
            .flatMapLatest { sortOrder -> playlistDao.getPlaylists(sortOrder = sortOrder.name) }
            .mapValues { it.asExternalModel() }
            .distinctUntilChanged()
    }

    override fun getPlaylistName(playlistId: Long): Flow<String> {
        return playlistDao.getPlaylistName(playlistId)
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist> {
        return playlistDao.getPlaylist(playlistId).map { it.asExternalModel() }
    }

    override suspend fun createPlaylist(playlistName: String): Long {
        return playlistDao.createPlaylist(PlaylistEntity(id = 0, playlistName = playlistName))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(PlaylistEntity(id = playlistId, playlistName = ""))
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val playlistSize = playlistDao.getPlaylistSize(playlistId = playlistId).first()
        playlistDao.addTrackToPlaylist(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = trackId,
                position = playlistSize,
            )
        )
    }

    override fun getTrackIdsInPlaylist(playlistId: Long): Flow<Set<Long>> {
        return playlistDao
            .getTrackIdsInPlaylist(playlistId = playlistId)
            .map { it.toSet() }
            .distinctUntilChanged()
    }

    override suspend fun removeItemFromPlaylist(playlistId: Long, position: Long) {
        playlistDao.removeItemFromPlaylist(playlistId = playlistId, position = position)
    }

    override suspend fun updatePlaylistName(playlistId: Long, playlistName: String) {
        playlistDao.updatePlaylist(PlaylistEntity(id = playlistId, playlistName = playlistName))
    }
}

fun PlaylistEntity.asExternalModel(): BasicPlaylist {
    return BasicPlaylist(id = id, name = playlistName)
}

fun PlaylistWithTracksEntity.asExternalModel(): Playlist {
    return Playlist(
        id = playlist.id,
        name = playlist.playlistName,
        tracks = tracks.map { it.asExternalModel() },
    )
}
