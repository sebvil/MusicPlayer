@file:Suppress("MagicNumber")

package com.sebastianvm.musicplayer.features.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.time.Duration.Companion.seconds

class TrackProgressStatePreviewParameterProvider : PreviewParameterProvider<TrackProgressState> {
    override val values = sequenceOf(
        TrackProgressState(
            currentPlaybackTime = 0.seconds,
            trackLength = 180.seconds
        ),
        TrackProgressState(
            currentPlaybackTime = 90.seconds,
            trackLength = 180.seconds
        ),
        TrackProgressState(
            currentPlaybackTime = 180.seconds,
            trackLength = 180.seconds
        )
    )
}
