package com.sebastianvm.musicplayer.repository.playlist

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.sebastianvm.model.BasicPlaylist
import com.sebastianvm.model.Playlist
import com.sebastianvm.musicplayer.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.database.entities.PlaylistEntity
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.asExternalModel
import com.sebastianvm.musicplayer.di.DispatcherProvider.ioDispatcher
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.util.extensions.mapValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistRepositoryImpl(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val playlistDao: PlaylistDao,
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<BasicPlaylist>> {
        return sortPreferencesRepository
            .getPlaylistsListSortOrder()
            .flatMapLatest { sortOrder -> playlistDao.getPlaylists(sortOrder = sortOrder) }
            .mapValues { it.asExternalModel() }
            .distinctUntilChanged()
    }

    override fun getPlaylistName(playlistId: Long): Flow<String> {
        return playlistDao.getPlaylistName(playlistId)
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist> {
        return playlistDao.getPlaylist(playlistId).map { it.asExternalModel() }
    }

    override fun createPlaylist(playlistName: String): Flow<Long?> {
        return flow {
            val id =
                try {
                    playlistDao.createPlaylist(
                        PlaylistEntity(
                            id = playlistName.hashCode().toLong(),
                            playlistName = playlistName,
                        ))
                } catch (e: SQLiteConstraintException) {
                    Log.i("Exception", e.message.orEmpty())
                    null
                }
            emit(id)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(PlaylistEntity(id = playlistId, playlistName = ""))
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val playlistSize = playlistDao.getPlaylistSize(playlistId = playlistId).first()
        withContext(ioDispatcher) {
            playlistDao.addTrackToPlaylist(
                PlaylistTrackCrossRef(
                    playlistId = playlistId,
                    trackId = trackId,
                    position = playlistSize,
                ))
        }
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
}
