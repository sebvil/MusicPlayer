package com.sebastianvm.musicplayer.repository

import androidx.annotation.StringRes
import androidx.paging.PagingSource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.database.daos.PlaylistFtsDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FullTextSearchRepository @Inject constructor(
    private val trackFtsDao: TrackFtsDao,
    private val artistFtsDao: ArtistFtsDao,
    private val albumFtsDao: AlbumFtsDao,
    private val genreFtsDao: GenreFtsDao,
    private val playlistFtsDao: PlaylistFtsDao
) {

    fun searchString(text: String) = "\"$text*\""

    fun searchTracksPaged(text: String): PagingSource<Int, FullTrackInfo> {
        return trackFtsDao.tracksWithTextPaged(text = searchString(text))
    }

    fun searchTracks(text: String): Flow<List<FullTrackInfo>> {
        return trackFtsDao.tracksWithText(searchString(text))
    }

    fun searchArtists(text: String): Flow<List<Artist>> {
        return artistFtsDao.artistsWithText(searchString(text))
    }

    fun searchAlbums(text: String): Flow<List<AlbumWithArtists>> {
        return albumFtsDao.albumsWithText(searchString(text))
    }

    fun searchGenres(text: String): Flow<List<Genre>> {
        return genreFtsDao.genresWithText(searchString(text))
    }

    fun searchPlaylists(text: String): Flow<List<Playlist>> {
        return playlistFtsDao.playlistsWithText(searchString(text))
    }
}

enum class SearchMode(@StringRes val res: Int) {
    SONGS(R.string.songs),
    ARTISTS(R.string.artists),
    ALBUMS(R.string.albums),
    GENRES(R.string.genres),
    PLAYLISTS(R.string.playlists)
}