package com.sebastianvm.musicplayer.core.services

import com.sebastianvm.musicplayer.core.data.di.RepositoryComponent
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager

interface Services : RepositoryComponent {
    val playbackManager: PlaybackManager
}
