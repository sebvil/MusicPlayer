package com.sebastianvm.musicplayer.features.player

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.core.designsystems.components.IconState
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString

enum class PlaybackIcon(val icon: IconState, @StringRes val contentDescription: Int) {
    PLAY(icon = AppIcons.PlayArrow, contentDescription = RString.play),
    PAUSE(icon = AppIcons.Pause, contentDescription = RString.pause)
}
