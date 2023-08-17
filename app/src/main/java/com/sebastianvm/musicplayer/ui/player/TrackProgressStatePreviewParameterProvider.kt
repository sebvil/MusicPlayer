@file:Suppress("MagicNumber")

package com.sebastianvm.musicplayer.ui.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class TrackProgressStatePreviewParameterProvider : PreviewParameterProvider<TrackProgressState> {
    override val values = sequenceOf(
        TrackProgressState(
            currentPlaybackTime = MinutesSecondsTime.fromMs(0),
            trackLength = MinutesSecondsTime.fromMs(180)
        ),
        TrackProgressState(
            currentPlaybackTime = MinutesSecondsTime.fromMs(90),
            trackLength = MinutesSecondsTime.fromMs(180)
        ),
        TrackProgressState(
            currentPlaybackTime = MinutesSecondsTime.fromMs(180),
            trackLength = MinutesSecondsTime.fromMs(180)
        )
    )
}
