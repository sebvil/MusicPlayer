package com.sebastianvm.musicplayer.repository.music

interface MusicRepository {
    suspend fun getMusic()
}
