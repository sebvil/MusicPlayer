package com.sebastianvm.musicplayer.repository

import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FullTextSearchRepository @Inject constructor(private val trackFtsDao: TrackFtsDao) {
    fun searchTracks(text: String): Flow<List<Track>> {
        return trackFtsDao.tracksWithText(text = "{*$text*}")
    }
}