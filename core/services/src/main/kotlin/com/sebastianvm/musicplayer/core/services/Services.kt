package com.sebastianvm.musicplayer.core.services

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface Services {
    val playbackManager: PlaybackManager
    val repositoryProvider: RepositoryProvider
    val featureRegistry: FeatureRegistry
}
