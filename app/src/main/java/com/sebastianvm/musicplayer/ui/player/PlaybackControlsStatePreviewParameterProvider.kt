package com.sebastianvm.musicplayer.ui.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

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
