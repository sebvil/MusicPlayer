package com.sebastianvm.musicplayer.repository

import android.content.Context
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val albumDao: AlbumDao
) {

    fun getAlbumsCount(): Flow<Long> {
        return albumDao.getAlbumsCount().distinctUntilChanged()
    }

    fun getAlbum(): Flow<List<FullAlbumInfo>> {
        return albumDao.getAlbums().distinctUntilChanged()
    }

    fun getAlbum(albumGids: List<String>): Flow<List<AlbumWithArtists>> {
        return albumDao.getAlbums(albumGids = albumGids).distinctUntilChanged()
    }

    fun getAlbum(albumGid: String): Flow<FullAlbumInfo> {
        return albumDao.getAlbum(albumGid = albumGid).distinctUntilChanged()
    }
}