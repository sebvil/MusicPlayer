package com.sebastianvm.musicplayer.repository.album

import android.content.Context
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val albumDao: AlbumDao
) : AlbumRepository {

    override fun getAlbumsCount(): Flow<Long> {
        return albumDao.getAlbumsCount().distinctUntilChanged()
    }

    override fun getAlbums(): Flow<List<AlbumWithArtists>> {
        return albumDao.getAlbums().distinctUntilChanged()
    }

    override fun getAlbums(albumIds: List<String>): Flow<List<AlbumWithArtists>> {
        return albumDao.getAlbums(albumIds = albumIds).distinctUntilChanged()
    }

    override fun getAlbum(albumId: String): Flow<FullAlbumInfo> {
        return albumDao.getAlbum(albumId = albumId).distinctUntilChanged()
    }

    override fun getAlbumWithTracks(albumId: String): Flow<Map<Album, List<FullTrackInfo>>> {
        return albumDao.getAlbumWithTracks(albumId).distinctUntilChanged()
    }
}
