package com.sebastianvm.musicplayer.features.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.MediaArtImageStatePreviewParamsProvider

class PlayerStatePreviewParameterProvider :
    PreviewParameterProvider<PlayerState.Playing> {
    override val values: Sequence<PlayerState.Playing> =
        MediaArtImageStatePreviewParamsProvider().values.flatMap { mediaArtImageState ->
            TrackProgressStatePreviewParameterProvider().values.flatMap { progressState ->
                PlaybackIcon.entries.flatMap { icon ->
                    TrackInfoStatePreviewParameterProvider().values.map { trackInfoState ->
                        PlayerState.FullScreenState(
                            mediaArtImageState = mediaArtImageState,
                            trackInfoState = trackInfoState,
                            playbackIcon = icon,
                            trackProgressState = progressState,
                        )
                    }
                }
            }
        }
}
