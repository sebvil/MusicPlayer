package com.sebastianvm.musicplayer.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.MediaArtImageStatePreviewParamsProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


class MusicPlayerViewStatePreviewParameterProvider :
    PreviewParameterProvider<MusicPlayerViewState> {
    override val values: Sequence<MusicPlayerViewState> =
        MediaArtImageStatePreviewParamsProvider().values.flatMap { mediaArtImageState ->
            PlaybackControlsStatePreviewParameterProvider().values.flatMap { playbackControlsState ->
                TrackInfoStatePreviewParameterProvider().values.map { trackInfoState ->
                    MusicPlayerViewState(
                        mediaArtImageState = mediaArtImageState,
                        trackInfoState = trackInfoState,
                        playbackControlsState = playbackControlsState
                    )
                }
            }
        }
}

@ComponentPreview
@Composable
fun MusicPlayerViewPreview(
    @PreviewParameter(
        MusicPlayerViewStatePreviewParameterProvider::class,
        limit = 1
    ) state: MusicPlayerViewState
) {
    ThemedPreview {
        MusicPlayerView(
            state = state,
            onProgressBarClicked = {},
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {},
        )
    }
}