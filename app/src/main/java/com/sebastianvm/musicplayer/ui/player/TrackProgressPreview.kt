package com.sebastianvm.musicplayer.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@ComponentPreview
@Composable
fun TrackProgressPreview(
    @PreviewParameter(TrackProgressStatePreviewParameterProvider::class) state: TrackProgressState
) {
    ThemedPreview {
        TrackProgress(state = state, onProgressBarClicked = {})
    }
}
