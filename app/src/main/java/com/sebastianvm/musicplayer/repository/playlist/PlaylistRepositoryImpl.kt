package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.database.entities.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(private val playlistDao: PlaylistDao) : PlaylistRepository {
    override fun getPlaylistsCount(): Flow<Long> {
        return playlistDao.getPlaylistsCount().distinctUntilChanged()
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists()
    }
}
