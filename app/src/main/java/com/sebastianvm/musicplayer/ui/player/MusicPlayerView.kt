package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState


data class MusicPlayerViewState(
    val mediaArtImageState: MediaArtImageState,
    val trackInfoState: TrackInfoState,
    val playbackControlsState: PlaybackControlsState
)


@Composable
fun MusicPlayerView(
    state: MusicPlayerViewState,
    modifier: Modifier = Modifier,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
) {
    BottomMusicPlayerView(
        state = state,
        modifier = modifier,
        onProgressBarClicked = onProgressBarClicked,
        onPreviousButtonClicked = onPreviousButtonClicked,
        onNextButtonClicked = onNextButtonClicked,
        onPlayToggled = onPlayToggled
    )
}

@Composable
fun BottomMusicPlayerView(
    state: MusicPlayerViewState,
    modifier: Modifier = Modifier,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
) {
    Row(modifier = modifier) {
        MediaArtImage(mediaArtImageState = state.mediaArtImageState)

        Column {
            TrackInfo(state = TrackInfoState(trackName = "Hello", artists = "Juanes"))

            PlaybackControls(
                state = state.playbackControlsState,
                onProgressBarClicked = onProgressBarClicked,
                onPreviousButtonClicked = onPreviousButtonClicked,
                onNextButtonClicked = onNextButtonClicked,
                onPlayToggled = onPlayToggled
            )
        }
    }
}