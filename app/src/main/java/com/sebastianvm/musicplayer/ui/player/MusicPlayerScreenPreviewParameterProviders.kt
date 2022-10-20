package com.sebastianvm.musicplayer.ui.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider


class TrackProgressStatePreviewParameterProvider : PreviewParameterProvider<TrackProgressState> {
    override val values = sequenceOf(
        TrackProgressState(
            currentPlaybackTimeMs = 61600,
            trackLengthMs = 184000
        ),
        TrackProgressState(
            currentPlaybackTimeMs = null,
            trackLengthMs = null
        )
    )
}

class TrackInfoStatePreviewParameterProvider : PreviewParameterProvider<TrackInfoState> {
    override val values = sequenceOf(
        TrackInfoState(
            trackName = "El Arrepentido",
            artists = "Melendi, Carlos Vives"
        )
    )
}
