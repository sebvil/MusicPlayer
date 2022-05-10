package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) :
    PlaylistRepository {
    override fun getPlaylistsCount(): Flow<Int> {
        return playlistDao.getPlaylistsCount().distinctUntilChanged()
    }

    override fun getPlaylists(sortOrder: MediaSortOrder): Flow<List<Playlist>> {
        return playlistDao.getPlaylists(sortOrder = sortOrder).distinctUntilChanged()
    }

    override suspend fun createPlaylist(playlistName: String) {
        withContext(ioDispatcher) {
            playlistDao.createPlaylist(Playlist(id = 0, playlistName = playlistName))
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        withContext(ioDispatcher) {
            playlistDao.deletePlaylist(Playlist(id = playlistId, playlistName = ""))
        }
    }
}
