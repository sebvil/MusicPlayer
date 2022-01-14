package com.sebastianvm.musicplayer.repository

import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val trackDao: TrackDao
) {

    fun getTracksCount(): Flow<Long> {
        return trackDao.getTracksCount().distinctUntilChanged()
    }

    fun getAllTracks(): Flow<List<FullTrackInfo>> {
        return trackDao.getAllTracks().distinctUntilChanged()
    }

    fun getTracks(tracksIds: List<String>): Flow<List<FullTrackInfo>> {
        return trackDao.getTracks(tracksIds).distinctUntilChanged()
    }

    fun getTrack(tracksId: String): Flow<FullTrackInfo> {
        return trackDao.getTrack(tracksId).distinctUntilChanged()
    }

    fun getTracksForArtist(artistName: String) : Flow<List<FullTrackInfo>> {
        return trackDao.getTracksForArtist(artistName)
    }

    fun getTracksForAlbum(albumId: String) : Flow<List<FullTrackInfo>> {
        return trackDao.getTracksForAlbum(albumId)
    }

    fun getTracksForGenre(genreName: String) :Flow<List<FullTrackInfo>> {
        return trackDao.getTracksForGenre(genreName)
    }

    fun getTracksForQueue(mediaGroup: MediaGroup): Flow<List<FullTrackInfo>> {
        return trackDao.getTracksForQueue(mediaGroup.mediaType, mediaGroup.mediaId)
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
