package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        onPlayToggled = onPlayToggled,
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
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MediaArtImage(
            mediaArtImageState = state.mediaArtImageState,
            modifier = Modifier
                .weight(0.25f)
                .padding(end = 8.dp)
        )

        Column(modifier = Modifier.weight(0.75f)) {
            TrackInfo(state = state.trackInfoState, style = MaterialTheme.typography.titleSmall)

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