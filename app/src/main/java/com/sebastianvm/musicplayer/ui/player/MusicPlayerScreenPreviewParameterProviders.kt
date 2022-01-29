package com.sebastianvm.musicplayer.ui.player

import android.net.Uri
import androidx.compose.ui.tooling.preview.PreviewParameterProvider


class MusicPlayerStatePreviewParameterProvider : PreviewParameterProvider<MusicPlayerState> {
    override val values = sequenceOf(
        MusicPlayerState(
            isPlaying = false,
            trackName = "El Arrepentido",
            artists = "Melendi, Carlos Vives",
            trackLengthMs = null,
            currentPlaybackTimeMs = null,
            trackArt = Uri.EMPTY
        )
    )
}

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
