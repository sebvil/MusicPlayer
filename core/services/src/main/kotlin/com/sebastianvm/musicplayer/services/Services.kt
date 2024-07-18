package com.sebastianvm.musicplayer.services

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.services.features.Features
import com.sebastianvm.musicplayer.services.playback.PlaybackManager

interface Services {
    val playbackManager: PlaybackManager
    val repositoryProvider: RepositoryProvider
    val features: Features
}
