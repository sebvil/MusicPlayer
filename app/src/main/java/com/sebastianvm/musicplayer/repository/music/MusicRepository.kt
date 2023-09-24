package com.sebastianvm.musicplayer.repository.music

import com.sebastianvm.musicplayer.repository.LibraryScanService

interface MusicRepository {
    suspend fun getMusic(messageCallback: LibraryScanService.MessageCallback)
}
