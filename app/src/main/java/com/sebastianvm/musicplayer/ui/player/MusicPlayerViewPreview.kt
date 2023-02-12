package com.sebastianvm.musicplayer.ui.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.MediaArtImageStatePreviewParamsProvider


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