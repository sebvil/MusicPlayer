package com.sebastianvm.musicplayer.core.services

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager

interface Services {
    val playbackManager: PlaybackManager
    val repositoryProvider: RepositoryProvider
}
