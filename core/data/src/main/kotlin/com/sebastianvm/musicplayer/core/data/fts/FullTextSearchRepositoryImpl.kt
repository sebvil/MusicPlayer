package com.sebastianvm.musicplayer.core.data.fts

import com.sebastianvm.musicplayer.core.common.extensions.mapValues
import com.sebastianvm.musicplayer.core.data.album.asExternalModel
import com.sebastianvm.musicplayer.core.data.artist.asExternalModel
import com.sebastianvm.musicplayer.core.data.genre.asExternalModel
import com.sebastianvm.musicplayer.core.data.playlist.asExternalModel
import com.sebastianvm.musicplayer.core.data.track.asExternalModel
import com.sebastianvm.musicplayer.core.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.core.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.core.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.core.database.daos.PlaylistFtsDao
import com.sebastianvm.musicplayer.core.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.core.model.AlbumWithArtists
import com.sebastianvm.musicplayer.core.model.BasicArtist
import com.sebastianvm.musicplayer.core.model.BasicGenre
import com.sebastianvm.musicplayer.core.model.BasicPlaylist
import com.sebastianvm.musicplayer.core.model.BasicTrack
import kotlinx.coroutines.flow.Flow

internal class FullTextSearchRepositoryImpl(
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
