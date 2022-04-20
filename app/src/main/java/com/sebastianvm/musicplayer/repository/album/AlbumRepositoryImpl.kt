package com.sebastianvm.musicplayer.repository.album

import android.content.Context
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val albumDao: AlbumDao
) : AlbumRepository {

    override fun getAlbumsCount(): Flow<Int> {
        return albumDao.getAlbumsCount().distinctUntilChanged()
    }

    override fun getAlbums(sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>): Flow<List<Album>> {
        return albumDao.getAllAlbums(sortOption = sortPreferences.sortOption, sortOrder = sortPreferences.sortOrder).distinctUntilChanged()
    }

    override fun getAlbums(albumIds: List<String>): Flow<List<Album>> {
        return albumDao.getAlbums(albumIds = albumIds).distinctUntilChanged()
    }

    override fun getAlbum(albumId: String): Flow<FullAlbumInfo> {
        return albumDao.getAlbum(albumId = albumId).distinctUntilChanged()
    }
}
