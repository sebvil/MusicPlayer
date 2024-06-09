package com.sebastianvm.musicplayer.repository.fts

import com.sebastianvm.musicplayer.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.database.daos.PlaylistFtsDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.asExternalModel
import com.sebastianvm.musicplayer.model.Album
import com.sebastianvm.musicplayer.model.BasicArtist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FullTextSearchRepositoryImpl(
    private val trackFtsDao: TrackFtsDao,
    private val artistFtsDao: ArtistFtsDao,
    private val albumFtsDao: AlbumFtsDao,
    private val genreFtsDao: GenreFtsDao,
    private val playlistFtsDao: PlaylistFtsDao,
) : FullTextSearchRepository {

    private fun searchString(text: String) = "\"$text*\""

    override fun searchTracks(text: String): Flow<List<BasicTrack>> {
        return trackFtsDao.tracksWithText(searchString(text))
    }

    override fun searchArtists(text: String): Flow<List<BasicArtist>> {
        return artistFtsDao.artistsWithText(searchString(text)).map { artists ->
            artists.map { it.asExternalModel() }
        }
    }

    override fun searchAlbums(text: String): Flow<List<Album>> {
        return albumFtsDao.albumsWithText(searchString(text)).map { albums ->
            albums.map { it.asExternalModel() }
        }
    }

    override fun searchGenres(text: String): Flow<List<Genre>> {
        return genreFtsDao.genresWithText(searchString(text))
    }

    override fun searchPlaylists(text: String): Flow<List<Playlist>> {
        return playlistFtsDao.playlistsWithText(searchString(text))
    }
}
