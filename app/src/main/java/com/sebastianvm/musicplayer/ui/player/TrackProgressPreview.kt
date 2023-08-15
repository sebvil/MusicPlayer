package com.sebastianvm.musicplayer.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

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

@ComponentPreview
@Composable
fun TrackProgressPreview(@PreviewParameter(TrackProgressStatePreviewParameterProvider::class) state: TrackProgressState) {
    ThemedPreview {
        TrackProgress(state = state, onProgressBarClicked = {})
    }
}
