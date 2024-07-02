package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
<<<<<<< HEAD
import com.sebastianvm.musicplayer.core.data.playback.PlaybackManager
=======
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager
import com.sebastianvm.musicplayer.core.playback.queue.QueueRepository
>>>>>>> 7be87a69 (progress)

interface Dependencies {
    val repositoryProvider: RepositoryProvider
    val playbackManager: PlaybackManager
}
