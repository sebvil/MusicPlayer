package com.sebastianvm.musicplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.entities.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val albumDao: AlbumDao
) {

    suspend fun insertAlbum(
        album: Album
    ): Long {
        return albumDao.insertAlbum(album)
    }

    suspend fun insertAlbumForArtists(albumGid: String, artistGids: List<String>) {
        return albumDao.insertAlbumForArtists(
            artistGids.map {
                AlbumsForArtist(albumGid, it)
            }
        )
    }

    suspend fun insertAppearsOnForArtists(albumGid: String, artistGids: List<String>) {
        return albumDao.insertAppearsOnForArtists(
            artistGids.map {
                AppearsOnForArtist(albumGid, it)
            }
        )
    }

    suspend fun getAlbumsCount(): Long {
        return albumDao.getAlbumsCount()
    }

    fun getAlbums(): LiveData<List<FullAlbumInfo>> {
        return albumDao.getAlbums()
    }

    fun getAlbums(albumGids: List<String>): LiveData<List<AlbumWithArtists>> {
        return albumDao.getAlbums(albumGids = albumGids)
    }

    fun getAlbumsForArtist(artistGid: String): LiveData<List<AlbumWithArtists>> {
        return albumDao.getAlbumsForArtist(artistGid)
    }

    fun getAppearsOnForArtist(artistGid: String): LiveData<List<AlbumWithArtists>> {
        return albumDao.getAppearsOnForArtist(artistGid)
    }
}