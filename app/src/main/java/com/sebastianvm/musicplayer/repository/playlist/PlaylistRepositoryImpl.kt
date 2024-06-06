package com.sebastianvm.musicplayer.repository.playlist

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.sebastianvm.musicplayer.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistWithTracks
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistRepositoryImpl(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val playlistDao: PlaylistDao,
    private val ioDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher,
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return sortPreferencesRepository
            .getPlaylistsListSortOrder()
            .flatMapLatest { sortOrder -> playlistDao.getPlaylists(sortOrder = sortOrder) }
            .distinctUntilChanged()
    }

    override fun getPlaylistName(playlistId: Long): Flow<String> {
        return playlistDao.getPlaylistName(playlistId)
    }

    override fun createPlaylist(playlistName: String): Flow<Long?> {
        return flow {
            val id =
                try {
                    withContext(ioDispatcher) {
                        playlistDao.createPlaylist(
                            Playlist(
                                id = playlistName.hashCode().toLong(),
                                playlistName = playlistName,
                            )
                        )
                    }
                } catch (e: SQLiteConstraintException) {
                    Log.i("Exception", e.message.orEmpty())
                    null
                }
            emit(id)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        withContext(ioDispatcher) {
            playlistDao.deletePlaylist(Playlist(id = playlistId, playlistName = ""))
        }
    }

    override fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks> {
        return playlistDao
            .getPlaylistWithTracks(playlistId = playlistId)
            .distinctUntilChanged()
            .mapNotNull { it }
    }

    override suspend fun addTrackToPlaylist(playlistTrackCrossRef: PlaylistTrackCrossRef) {
        withContext(ioDispatcher) { playlistDao.addTrackToPlaylist(playlistTrackCrossRef) }
    }

    override fun getPlaylistSize(playlistId: Long): Flow<Long> {
        return playlistDao.getPlaylistSize(playlistId = playlistId).distinctUntilChanged()
    }

    override fun getTrackIdsInPlaylist(playlistId: Long): Flow<Set<Long>> {
        return playlistDao
            .getTrackIdsInPlaylist(playlistId = playlistId)
            .map { it.toSet() }
            .flowOn(defaultDispatcher)
            .distinctUntilChanged()
    }

    override fun getTracksInPlaylist(playlistId: Long): Flow<List<TrackWithPlaylistPositionView>> {
        return sortPreferencesRepository
            .getPlaylistSortPreferences(playlistId = playlistId)
            .flatMapLatest { sortPreferences ->
                playlistDao.getTracksInPlaylist(
                    playlistId = playlistId,
                    sortOption = sortPreferences.sortOption,
                    sortOrder = sortPreferences.sortOrder,
                )
            }
            .distinctUntilChanged()
    }

    override suspend fun removeItemFromPlaylist(playlistId: Long, position: Long) {
        withContext(ioDispatcher) {
            playlistDao.removeItemFromPlaylist(playlistId = playlistId, position = position)
        }
    }
}
