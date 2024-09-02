package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.queue.QueueFeature

class DefaultQueueFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
) : QueueFeature {
    override fun queueUiComponent(): MvvmComponent {
        return QueueMvvmComponent(
            queueRepository = repositoryProvider.queueRepository,
            playbackManager = playbackManager,
        )
    }
}
