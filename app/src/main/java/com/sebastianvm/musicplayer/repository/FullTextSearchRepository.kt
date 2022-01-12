package com.sebastianvm.musicplayer.repository

import androidx.paging.PagingSource
import com.sebastianvm.musicplayer.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FullTextSearchRepository @Inject constructor(
    private val trackFtsDao: TrackFtsDao,
    private val artistFtsDao: ArtistFtsDao,
    private val albumFtsDao: AlbumFtsDao,
    private val genreFtsDao: GenreFtsDao,
) {


    fun searchTracks(text: String): PagingSource<Int, FullTrackInfo> {
        return trackFtsDao.tracksWithText(text = "\"$text*\"")
    }

    fun searchArtists(text: String): PagingSource<Int, Artist> {
        return artistFtsDao.artistsWithText(text = "{\"$text*\"}")
    }

    fun searchAlbums(text: String): PagingSource<Int, AlbumWithArtists> {
        return albumFtsDao.albumsWithText(text = "{\"$text*\"}")
    }

    fun searchGenres(text: String): PagingSource<Int, Genre> {
        return genreFtsDao.genresWithText(text = "{\"$text*\"}")
    }
}