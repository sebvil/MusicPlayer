package com.sebastianvm.musicplayer.ui.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.MediaArtImageStatePreviewParamsProvider

class PlayerViewStatePreviewParameterProvider :
    PreviewParameterProvider<PlayerViewState> {
    override val values: Sequence<PlayerViewState> =
        MediaArtImageStatePreviewParamsProvider().values.flatMap { mediaArtImageState ->
            PlaybackControlsStatePreviewParameterProvider().values.flatMap { playbackControlsState ->
                TrackInfoStatePreviewParameterProvider().values.map { trackInfoState ->
                    PlayerViewState(
                        mediaArtImageState = mediaArtImageState,
                        trackInfoState = trackInfoState,
                        playbackControlsState = playbackControlsState
                    )
                }
            }
        }
}
