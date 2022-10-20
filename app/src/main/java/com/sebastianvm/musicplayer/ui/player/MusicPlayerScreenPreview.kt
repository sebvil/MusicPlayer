package com.sebastianvm.musicplayer.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider


class TrackProgressStatePreviewParameterProvider : PreviewParameterProvider<TrackProgressState> {
    override val values = sequenceOf(
        TrackProgressState(
            currentPlaybackTimeMs = 0,
            trackLengthMs = 184000
        ),
        TrackProgressState(
            currentPlaybackTimeMs = 184000,
            trackLengthMs = 184000
        ),
        TrackProgressState(
            currentPlaybackTimeMs = 100000,
            trackLengthMs = 184000
        ),
        TrackProgressState(
            currentPlaybackTimeMs = null,
            trackLengthMs = null
        )
    )
}

class TrackInfoStatePreviewParameterProvider : PreviewParameterProvider<TrackInfoState> {
    override val values = sequenceOf(
        TrackInfoState(
            trackName = PreviewUtil.randomString(minLength = 40),
            artists = PreviewUtil.randomString(minLength = 40)
        ),
        TrackInfoState(
            trackName = PreviewUtil.randomString(maxLength = 15),
            artists = PreviewUtil.randomString(maxLength = 15)
        ),
        TrackInfoState(
            trackName = PreviewUtil.randomString(maxLength = 15),
            artists = PreviewUtil.randomString(minLength = 40)
        ),
        TrackInfoState(
            trackName = PreviewUtil.randomString(minLength = 40),
            artists = PreviewUtil.randomString(maxLength = 15)
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
fun TrackProgressPreview(@PreviewParameter(TrackProgressStatePreviewParameterProvider::class) state: TrackProgressState) {
    ThemedPreview {
        TrackProgress(trackProgressState = state)
    }
}

@ComponentPreview
@Composable
fun TrackInfoPreview(@PreviewParameter(TrackInfoStatePreviewParameterProvider::class) state: TrackInfoState) {
    ThemedPreview {
        TrackInfo(state = state)
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