package com.sebastianvm.musicplayer.repository.album

import android.content.Context
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithTracks
import com.sebastianvm.musicplayer.database.entities.BasicAlbum
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val albumDao: AlbumDao
) : AlbumRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAlbums(): Flow<List<Album>> {
        return sortPreferencesRepository.getAlbumListSortPreferences()
            .flatMapLatest { sortPreferences ->
                albumDao.getAllAlbums(
                    sortOption = sortPreferences.sortOption,
                    sortOrder = sortPreferences.sortOrder
                ).distinctUntilChanged()
            }
    }

    override fun getFullAlbumInfo(albumId: Long): Flow<FullAlbumInfo> {
        return albumDao.getFullAlbumInfo(albumId = albumId).distinctUntilChanged()
    }

    override fun getAlbum(albumId: Long): Flow<BasicAlbum> {
        return albumDao.getAlbum(albumId = albumId)
    }

    override fun getAlbumWithTracks(albumId: Long): Flow<AlbumWithTracks> {
        return albumDao.getAlbumWithTracks(albumId).distinctUntilChanged()
    }
}
