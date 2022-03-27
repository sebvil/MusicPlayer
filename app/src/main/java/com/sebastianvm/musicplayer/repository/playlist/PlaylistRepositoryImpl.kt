package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
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

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().distinctUntilChanged()
    }

    override suspend fun createPlaylist(playlistName: String) {
        withContext(ioDispatcher) {
            playlistDao.createPlaylist(Playlist(playlistName = playlistName))
        }
    }

    override suspend fun deletePlaylist(playlistName: String) {
        withContext(ioDispatcher) {
            playlistDao.deletePlaylist(Playlist(playlistName = playlistName))
        }
    }
}
