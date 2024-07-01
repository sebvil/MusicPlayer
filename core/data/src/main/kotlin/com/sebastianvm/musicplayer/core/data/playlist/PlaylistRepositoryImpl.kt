package com.sebastianvm.musicplayer.core.data.playlist

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.sebastianvm.musicplayer.core.common.extensions.mapValues
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.data.track.asExternalModel
import com.sebastianvm.musicplayer.core.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.core.database.entities.PlaylistEntity
import com.sebastianvm.musicplayer.core.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.PlaylistWithTracksEntity
import com.sebastianvm.musicplayer.core.model.BasicPlaylist
import com.sebastianvm.musicplayer.core.model.Playlist
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlaylistRepositoryImpl(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val playlistDao: PlaylistDao,
    private val ioDispatcher: CoroutineDispatcher,
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

    override fun createPlaylist(playlistName: String): Flow<Long?> {
        return flow {
            val id =
                try {
                    playlistDao.createPlaylist(
                        PlaylistEntity(
                            id = playlistName.hashCode().toLong(),
                            playlistName = playlistName,
                        )
                    )
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
                )
            )
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
