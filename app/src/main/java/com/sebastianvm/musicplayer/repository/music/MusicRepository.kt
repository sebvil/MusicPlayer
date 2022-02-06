package com.sebastianvm.musicplayer.repository.music

import com.sebastianvm.musicplayer.repository.LibraryScanService
import kotlinx.coroutines.flow.Flow


interface MusicRepository {
    fun getCounts(): Flow<CountHolder>
    suspend fun getMusic(messageCallback: LibraryScanService.MessageCallback)
}
