package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.Player
import com.sebastianvm.musicplayer.player.PlaybackInfo
import kotlinx.coroutines.flow.Flow

interface PlaybackInfoRepository {
    suspend fun modifySavedPlaybackInfo(player: Player)
    fun getSavedPlaybackInfo(): Flow<PlaybackInfo>
}