package com.sebastianvm.musicplayer.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


class PlaybackControlsStatePreviewParameterProvider :
    PreviewParameterProvider<PlaybackControlsState> {
    override val values: Sequence<PlaybackControlsState> =
        TrackProgressStatePreviewParameterProvider().values.flatMap { progressState ->
            PlaybackIcon.values().map {
                PlaybackControlsState(
                    trackProgressState = progressState,
                    playbackIcon = it
                )
            }
        }
}


@ComponentPreview
@Composable
fun PlaybackControlsPreview(@PreviewParameter(PlaybackControlsStatePreviewParameterProvider::class) state: PlaybackControlsState) {
    ThemedPreview {
        PlaybackControls(
            state = state,
            onProgressBarClicked = {},
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {})
    }
}