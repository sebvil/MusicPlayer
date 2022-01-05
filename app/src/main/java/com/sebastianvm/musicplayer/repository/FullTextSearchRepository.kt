package com.sebastianvm.musicplayer.repository

import com.sebastianvm.musicplayer.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FullTextSearchRepository @Inject constructor(
    private val trackFtsDao: TrackFtsDao,
    private val artistFtsDao: ArtistFtsDao,
    private val albumFtsDao: AlbumFtsDao,
    private val genreFtsDao: GenreFtsDao
) {
    fun searchTracks(text: String): Flow<List<FullTrackInfo>> {
        return trackFtsDao.tracksWithText(text = "{\"$text*\"}").distinctUntilChanged()
    }

    fun searchArtists(text: String): Flow<List<Artist>> {
        return artistFtsDao.artistsWithText(text = "{\"$text*\"}").distinctUntilChanged()
    }

    fun searchAlbums(text: String): Flow<List<AlbumWithArtists>> {
        return albumFtsDao.albumsWithText(text = "{\"$text*\"}").distinctUntilChanged()
    }

    fun searchGenres(text: String): Flow<List<Genre>> {
        return genreFtsDao.genresWithText(text = "{\"$text*\"}").distinctUntilChanged()
    }
}