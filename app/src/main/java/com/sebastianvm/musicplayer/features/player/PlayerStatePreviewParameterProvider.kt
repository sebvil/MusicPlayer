package com.sebastianvm.musicplayer.features.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class PlayerStatePreviewParameterProvider : PreviewParameterProvider<PlayerState.Playing> {
    override val values: Sequence<PlayerState.Playing> =
        TrackProgressStatePreviewParameterProvider().values.flatMap { progressState ->
            PlaybackIcon.entries.flatMap { icon ->
                TrackInfoStatePreviewParameterProvider().values.map { trackInfoState ->
                    PlayerState.FullScreenState(
                        artworkUri = "",
                        trackInfoState = trackInfoState,
                        playbackIcon = icon,
                        trackProgressState = progressState,
                    )
                }
            }
        }
}
