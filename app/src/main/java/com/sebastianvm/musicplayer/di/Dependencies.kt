package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.core.data.di.HasRepositoryProvider
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager

interface Dependencies : HasRepositoryProvider {
    val playbackManager: PlaybackManager
}
