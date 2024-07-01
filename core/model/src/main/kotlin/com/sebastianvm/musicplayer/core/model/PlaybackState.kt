package com.sebastianvm.musicplayer.core.model

import kotlin.time.Duration

sealed interface PlaybackState

data class TrackPlayingState(
    val trackInfo: TrackInfo,
    val isPlaying: Boolean,
    val currentTrackProgress: Duration,
) : PlaybackState

data object NotPlayingState : PlaybackState

data class TrackInfo(
    val title: String,
    val artists: String,
    val artworkUri: String,
    val trackLength: Duration,
)
