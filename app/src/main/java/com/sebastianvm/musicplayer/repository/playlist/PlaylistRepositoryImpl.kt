package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistWithTracks
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import com.sebastianvm.musicplayer.util.coroutines.DefaultDispatcher
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : PlaylistRepository {
    override fun getPlaylistsCount(): Flow<Int> {
        return playlistDao.getPlaylistsCount().distinctUntilChanged()
    }

    override fun getPlaylists(sortOrder: MediaSortOrder): Flow<List<Playlist>> {
        return playlistDao.getPlaylists(sortOrder = sortOrder).distinctUntilChanged()
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylist(playlistId)
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

    override fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks> {
        return playlistDao.getPlaylistWithTracks(playlistId = playlistId).distinctUntilChanged()
            .mapNotNull { it }
    }

    override suspend fun addTrackToPlaylist(playlistTrackCrossRef: PlaylistTrackCrossRef) {
        withContext(ioDispatcher) {
            playlistDao.addTrackToPlaylist(playlistTrackCrossRef)
        }
    }

    override fun getPlaylistSize(playlistId: Long): Flow<Long> {
        return playlistDao.getPlaylistSize(playlistId = playlistId).distinctUntilChanged()
    }

    override fun getTrackIdsInPlaylist(playlistId: Long): Flow<Set<Long>> {
        return playlistDao.getTrackIdsInPlaylist(playlistId = playlistId).map { it.toSet() }
            .flowOn(defaultDispatcher).distinctUntilChanged()
    }

    override fun getTracksInPlaylist(
        playlistId: Long,
        sortPreferences: MediaSortPreferences<SortOptions.PlaylistSortOptions>
    ): Flow<List<TrackWithPlaylistPositionView>> {
        return playlistDao.getTracksInPlaylist(
            playlistId = playlistId,
            sortOption = sortPreferences.sortOption,
            sortOrder = sortPreferences.sortOrder
        ).distinctUntilChanged()
    }
}
