package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.data.playback.PlaybackManager

interface Dependencies {
    val repositoryProvider: RepositoryProvider
    val playbackManager: PlaybackManager
}
