package com.sebastianvm.musicplayer.ui.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class TrackProgressStatePreviewParameterProvider : PreviewParameterProvider<TrackProgressState> {
    override val values = sequenceOf(
        TrackProgressState(
            progress = 0.percent,
            currentPlaybackTime = "0:00",
            trackLength = "3:00"
        ),
        TrackProgressState(
            progress = 50.percent,
            currentPlaybackTime = "1:30",
            trackLength = "3:00"
        ),
        TrackProgressState(
            progress = 100.percent,
            currentPlaybackTime = "3:00",
            trackLength = "3:00"
        )
    )
}
