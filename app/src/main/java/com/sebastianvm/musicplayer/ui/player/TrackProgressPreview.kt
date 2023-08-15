package com.sebastianvm.musicplayer.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreviews
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@ComponentPreviews
@Composable
private fun TrackProgressPreview(
    @PreviewParameter(TrackProgressStatePreviewParameterProvider::class) state: TrackProgressState
) {
    ThemedPreview {
        TrackProgress(state = state, onProgressBarClicked = {})
    }
}
