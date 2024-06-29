package com.sebastianvm.musicplayer.features.player

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.sebastianvm.resources.RString

enum class PlaybackIcon(val icon: ImageVector, @StringRes val contentDescription: Int) {
    PLAY(icon = Icons.Default.PlayArrow, contentDescription = RString.play),
    PAUSE(icon = Icons.Default.Pause, contentDescription = RString.pause)
}
