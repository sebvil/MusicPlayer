package com.sebastianvm.musicplayer.repository.fts

import com.sebastianvm.musicplayer.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.database.daos.PlaylistFtsDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FullTextSearchRepositoryImpl @Inject constructor(
    private val trackFtsDao: TrackFtsDao,
    private val artistFtsDao: ArtistFtsDao,
    private val albumFtsDao: AlbumFtsDao,
    private val genreFtsDao: GenreFtsDao,
    private val playlistFtsDao: PlaylistFtsDao
) : FullTextSearchRepository {

    private fun searchString(text: String) = "\"$text*\""

    override fun searchTracks(text: String): Flow<List<BasicTrack>> {
        return trackFtsDao.tracksWithText(searchString(text))
    }

    override fun searchArtists(text: String): Flow<List<Artist>> {
        return artistFtsDao.artistsWithText(searchString(text))
    }

    override fun searchAlbums(text: String): Flow<List<Album>> {
        return albumFtsDao.albumsWithText(searchString(text))
    }

    override fun searchGenres(text: String): Flow<List<Genre>> {
        return genreFtsDao.genresWithText(searchString(text))
    }

    override fun searchPlaylists(text: String): Flow<List<Playlist>> {
        return playlistFtsDao.playlistsWithText(searchString(text))
    }
}