package com.sebastianvm.musicplayer.features.player

import kotlin.time.Duration

@JvmInline
value class Percentage(val percent: Float) {
    companion object {
        const val MAX = 100f
    }
}

data class TrackProgressState(val currentPlaybackTime: Duration, val trackLength: Duration) {
    val progress: Percentage
        get() =
            if (trackLength == Duration.ZERO) {
                Percentage(0f)
            } else {
                Percentage(
                    currentPlaybackTime.inWholeMilliseconds.toFloat() /
                        trackLength.inWholeMilliseconds.toFloat()
                )
            }
}
