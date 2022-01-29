package com.sebastianvm.musicplayer.repository.music

import com.sebastianvm.musicplayer.repository.LibraryScanService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMusicRepository : MusicRepository {
    override fun getCounts(): Flow<CountHolder> = flow {
        emit(
            CountHolder(
                tracks = FAKE_TRACK_COUNTS,
                artists = FAKE_ARTIST_COUNTS,
                albums = FAKE_ALBUM_COUNTS,
                genres = FAKE_GENRE_COUNTS
            )
        )
    }

    override suspend fun getMusic(messageCallback: LibraryScanService.MessageCallback) = Unit

    companion object {
        const val FAKE_TRACK_COUNTS = 1000L
        const val FAKE_ARTIST_COUNTS = 10L
        const val FAKE_ALBUM_COUNTS = 100L
        const val FAKE_GENRE_COUNTS = 1L
    }
}
