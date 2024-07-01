package com.sebastianvm.musicplayer.core.data.music

interface MusicRepository {
    suspend fun getMusic()
}
