package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.data.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.data.queue.QueueRepository

interface Dependencies {
    val repositoryProvider: RepositoryProvider
    val queueRepository: QueueRepository
    val playbackManager: PlaybackManager
}
