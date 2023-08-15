package com.sebastianvm.musicplayer.ui.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

enum class PlaybackIcon(val icon: ImageVector) {
    PLAY(icon = Icons.Default.PlayArrow),
    PAUSE(icon = Icons.Default.Pause)
}

data class PlaybackControlsState(
    val trackProgressState: TrackProgressState,
    val playbackIcon: PlaybackIcon
)
