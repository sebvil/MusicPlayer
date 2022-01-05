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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FullTextSearchRepository @Inject constructor(
    private val trackFtsDao: TrackFtsDao,
    private val artistFtsDao: ArtistFtsDao,
    private val albumFtsDao: AlbumFtsDao,
    private val genreFtsDao: GenreFtsDao,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository,
) {

    fun searchTracks(text: String): Flow<Set<FullTrackInfo>> {
        val tracks = trackFtsDao.tracksWithText(text = "{\"$text*\"}").map {
            it.toMutableSet()
        }
        val artistTracks = artistFtsDao.artistsWithText(text = "{\"$text*\"}").map { artists ->
            artists.foldRight(mutableSetOf<FullTrackInfo>()) { artist, currentSet ->
                currentSet.addAll(trackRepository.getTracksForArtist(artist.artistId).first())
                currentSet
            }
        }
        return tracks.combine(artistTracks) { allTracks, tracksForArtists ->
            allTracks.addAll(tracksForArtists)
            allTracks
        }.distinctUntilChanged()
    }

    fun searchArtists(text: String): Flow<List<Artist>> {
        return artistFtsDao.artistsWithText(text = "{\"$text*\"}").distinctUntilChanged()
    }

    fun searchAlbums(text: String): Flow<Set<AlbumWithArtists>> {
        val albums = albumFtsDao.albumsWithText(text = "{\"$text*\"}").map { it.toMutableSet() }
        val artistAlbums = artistFtsDao.artistsWithText(text = "{\"$text*\"}").map { artists ->
            artists.foldRight(mutableSetOf<AlbumWithArtists>()) { artist, currentSet ->
                currentSet.addAll(albumRepository.getAlbumsForArtist(artist.artistId).first())
                currentSet
            }
        }

        return albums.combine(artistAlbums) { allAlbums, albumsForArtists ->
            allAlbums.addAll(albumsForArtists)
            allAlbums
        }.distinctUntilChanged()
    }

    fun searchGenres(text: String): Flow<List<Genre>> {
        return genreFtsDao.genresWithText(text = "{\"$text*\"}").distinctUntilChanged()
    }
}