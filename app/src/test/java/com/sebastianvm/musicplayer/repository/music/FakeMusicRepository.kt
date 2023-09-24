package com.sebastianvm.musicplayer.repository.music

import com.sebastianvm.musicplayer.repository.LibraryScanService
import kotlinx.coroutines.flow.Flow

class FakeMusicRepository(private val countHolderFlow: Flow<CountHolder>) : MusicRepository {

    override suspend fun getMusic(messageCallback: LibraryScanService.MessageCallback) = Unit
}
