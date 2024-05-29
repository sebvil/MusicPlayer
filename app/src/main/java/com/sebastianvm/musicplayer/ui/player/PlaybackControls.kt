package com.sebastianvm.musicplayer.ui.player

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.sebastianvm.musicplayer.R

enum class PlaybackIcon(val icon: ImageVector, @StringRes val contentDescription: Int) {
    PLAY(icon = Icons.Default.PlayArrow, contentDescription = R.string.play),
    PAUSE(icon = Icons.Default.Pause, contentDescription = R.string.pause)
}

data class PlaybackControlsState(
    val trackProgressState: TrackProgressState,
    val playbackIcon: PlaybackIcon
)
