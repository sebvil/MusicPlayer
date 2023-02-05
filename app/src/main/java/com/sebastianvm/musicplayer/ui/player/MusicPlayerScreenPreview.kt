package com.sebastianvm.musicplayer.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider


class OldTrackProgressStatePreviewParameterProvider :
    PreviewParameterProvider<OldTrackProgressState> {
    override val values = sequenceOf(
        OldTrackProgressState(
            currentPlaybackTimeMs = 0,
            trackLengthMs = 184000
        ),
        OldTrackProgressState(
            currentPlaybackTimeMs = 184000,
            trackLengthMs = 184000
        ),
        OldTrackProgressState(
            currentPlaybackTimeMs = 100000,
            trackLengthMs = 184000
        ),
        OldTrackProgressState(
            currentPlaybackTimeMs = null,
            trackLengthMs = null
        )
    )
}


class MusicPlayerStatePreviewParamsProvider : PreviewParameterProvider<MusicPlayerState> {
    override val values: Sequence<MusicPlayerState>
        get() = sequenceOf(
            MusicPlayerState(
                isPlaying = true,
                trackName = PreviewUtil.randomString(),
                artists = PreviewUtil.randomString(),
                trackLengthMs = 184000,
                currentPlaybackTimeMs = 0,
                trackArt = ""
            ),
            MusicPlayerState(
                isPlaying = false,
                trackName = PreviewUtil.randomString(),
                artists = PreviewUtil.randomString(),
                trackLengthMs = 184000,
                currentPlaybackTimeMs = 18000,
                trackArt = ""
            ),
            MusicPlayerState(
                isPlaying = false,
                trackName = null,
                artists = null,
                trackLengthMs = 0,
                currentPlaybackTimeMs = 0,
                trackArt = ""
            )

        )
}

@ComponentPreview
@Composable
fun TrackProgressPreview(@PreviewParameter(OldTrackProgressStatePreviewParameterProvider::class) state: OldTrackProgressState) {
    ThemedPreview {
        TrackProgress(trackProgressState = state)
    }
}

@ScreenPreview
@Composable
fun MusicPlayerScreenPreview(@PreviewParameter(MusicPlayerStatePreviewParamsProvider::class) state: MusicPlayerState) {
    ScreenPreview {
        MusicPlayerScreen(
            state = state,
            screenDelegate = DefaultScreenDelegateProvider.getDefaultInstance()
        )
    }
}