package com.sebastianvm.musicplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.entities.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val trackDao: TrackDao
) {

    fun getTracksCount(): LiveData<Long> {
        return trackDao.getTracksCount()
    }

    fun getAllTracks(): LiveData<List<FullTrackInfo>> {
        return trackDao.getAllTracks()
    }

    fun getTracks(tracksGids: List<String>): LiveData<List<FullTrackInfo>> {
        return trackDao.getTracks(tracksGids)
    }

    suspend fun newInsertTrack(
        track: Track,
        artistTrackCrossRefs: List<ArtistTrackCrossRef>,
        genreTrackCrossRef: List<GenreTrackCrossRef>,
        artists: List<Artist>,
        genres: List<Genre>,
        album: Album,
        albumForArtists: List<AlbumsForArtist>,
        appearsOnForArtist: List<AppearsOnForArtist>
    ) {
        trackDao.newInsertTrack(
            track = track,
            artistTrackCrossRefs = artistTrackCrossRefs,
            genreTrackCrossRef = genreTrackCrossRef,
            artists = artists,
            genres = genres,
            album = album,
            albumForArtists = albumForArtists,
            appearsOnForArtist = appearsOnForArtist,
        )
    }
}