package com.sebastianvm.musicplayer.features.api.player

import com.sebastianvm.musicplayer.core.ui.mvvm.Props

data class PlayerProps(val isFullscreen: Boolean, val dismissFullScreenPlayer: () -> Unit) : Props
