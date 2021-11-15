package com.sebastianvm.musicplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val artistDao: ArtistDao
) {

    suspend fun insertArtists(
        artistGids: List<String>,
        artistNames: List<String>
    ): List<Long> {
        return artistDao.insertArtists(
            (artistGids zip artistNames).map {
                Artist(it.first, it.second)
            }
        )
    }

    suspend fun getArtistsCount(): Long {
        return artistDao.getArtistsCount()
    }

    fun getArtists(): LiveData<List<ArtistWithAlbums>> {
        return artistDao.getArtists()
    }

    fun getArtist(artistGid: String): LiveData<Artist> {
        return artistDao.getArtist(artistGid)
    }
}