package com.sebastianvm.musicplayer.repository

import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val trackDao: TrackDao
) {

    fun getTracksCount(): Flow<Long> {
        return trackDao.getTracksCount()
    }

    fun getAllTracks(): Flow<List<FullTrackInfo>> {
        return trackDao.getAllTracks()
    }

    fun getTracks(tracksGids: List<String>): Flow<List<FullTrackInfo>> {
        return trackDao.getTracks(tracksGids)
    }

    suspend fun insertAllTracks(
        tracks: Set<Track>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<Artist>,
        genres: Set<Genre>,
        albums: Set<Album>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>
    ) {
        trackDao.insertAllTracks(
            tracks = tracks,
            artistTrackCrossRefs = artistTrackCrossRefs,
            genreTrackCrossRefs = genreTrackCrossRefs,
            artists = artists,
            genres = genres,
            albums = albums,
            albumsForArtists = albumsForArtists,
            appearsOnForArtists = appearsOnForArtists,
        )
    }
}