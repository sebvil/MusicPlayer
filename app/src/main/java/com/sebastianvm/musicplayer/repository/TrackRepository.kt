package com.sebastianvm.musicplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val trackDao: TrackDao
) {

    suspend fun insertTrack(
        track: Track,
        artistGids: List<String>,
        genreNames: List<String>,
    ) {
        val artistTrackCrossRefs = artistGids.map { ArtistTrackCrossRef(it, track.trackGid) }
        val genreTrackCrossRefs = genreNames.map { GenreTrackCrossRef(it, track.trackGid) }
        trackDao.insertTrack(track, artistTrackCrossRefs, genreTrackCrossRefs)
    }

    suspend fun getTracksCount(): Long {
        return trackDao.getTracksCount()
    }

    fun getAllTracks(): LiveData<List<FullTrackInfo>> {
        return trackDao.getAllTracks()
    }

    fun getTracks(tracksGids: List<String>): LiveData<List<FullTrackInfo>> {
        return trackDao.getTracks(tracksGids)
    }
}