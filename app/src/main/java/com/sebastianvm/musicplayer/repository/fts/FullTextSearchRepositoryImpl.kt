package com.sebastianvm.musicplayer.repository.fts

import com.sebastianvm.model.AlbumWithArtists
import com.sebastianvm.model.BasicArtist
import com.sebastianvm.model.BasicGenre
import com.sebastianvm.model.BasicPlaylist
import com.sebastianvm.model.BasicTrack
import com.sebastianvm.musicplayer.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.database.daos.PlaylistFtsDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.asExternalModel
import com.sebastianvm.musicplayer.util.extensions.mapValues
import kotlinx.coroutines.flow.Flow

class FullTextSearchRepositoryImpl(
    private val trackFtsDao: TrackFtsDao,
    private val artistFtsDao: ArtistFtsDao,
    private val albumFtsDao: AlbumFtsDao,
    private val genreFtsDao: GenreFtsDao,
    private val playlistFtsDao: PlaylistFtsDao,
) : FullTextSearchRepository {

    private fun searchString(text: String) = "\"$text*\""

    override fun searchTracks(text: String): Flow<List<BasicTrack>> {
        return trackFtsDao.tracksWithText(searchString(text)).mapValues { it.asExternalModel() }
    }

    override fun searchArtists(text: String): Flow<List<BasicArtist>> {
        return artistFtsDao.artistsWithText(searchString(text)).mapValues { it.asExternalModel() }
    }

    override fun searchAlbums(text: String): Flow<List<AlbumWithArtists>> {
        return albumFtsDao.albumsWithText(searchString(text)).mapValues { it.asExternalModel() }
    }

    override fun searchGenres(text: String): Flow<List<BasicGenre>> {
        return genreFtsDao.genresWithText(searchString(text)).mapValues { it.asExternalModel() }
    }

    override fun searchPlaylists(text: String): Flow<List<BasicPlaylist>> {
        return playlistFtsDao.playlistsWithText(searchString(text)).mapValues {
            it.asExternalModel()
        }
    }
}
