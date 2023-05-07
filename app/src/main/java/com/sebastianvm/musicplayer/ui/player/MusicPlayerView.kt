package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


data class MusicPlayerViewState(
    val mediaArtImageState: MediaArtImageState,
    val trackInfoState: TrackInfoState,
    val playbackControlsState: PlaybackControlsState
)


enum class PlayerViewMode {
    BOTTOM_BAR,
    SIDE_BAR,
    FULL_SCREEN
}

@Composable
fun MusicPlayerView(
    state: MusicPlayerViewState,
    playerViewMode: PlayerViewMode,
    modifier: Modifier = Modifier,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    onToggleExpandedState: () -> Unit
) {
    when (playerViewMode) {
        PlayerViewMode.BOTTOM_BAR -> {
            HorizontalMusicPlayerView(
                state = state,
                modifier = modifier.clickable { onToggleExpandedState() },
                onProgressBarClicked = onProgressBarClicked,
                onPreviousButtonClicked = onPreviousButtonClicked,
                onNextButtonClicked = onNextButtonClicked,
                onPlayToggled = onPlayToggled,
            )
        }

        PlayerViewMode.SIDE_BAR -> {
            VerticalMusicPlayerView(
                state = state,
                modifier = modifier.clickable { onToggleExpandedState() },
                onProgressBarClicked = onProgressBarClicked,
                onPreviousButtonClicked = onPreviousButtonClicked,
                onNextButtonClicked = onNextButtonClicked,
                onPlayToggled = onPlayToggled,
            )
        }

        PlayerViewMode.FULL_SCREEN -> {
            VerticalMusicPlayerView(
                state = state,
                modifier = modifier.clickable { onToggleExpandedState() },
                onProgressBarClicked = onProgressBarClicked,
                onPreviousButtonClicked = onPreviousButtonClicked,
                onNextButtonClicked = onNextButtonClicked,
                onPlayToggled = onPlayToggled,
            )
        }
    }

}

@Composable
fun HorizontalMusicPlayerView(
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

@Composable
fun VerticalMusicPlayerView(
    state: MusicPlayerViewState,
    modifier: Modifier = Modifier,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MediaArtImage(
            mediaArtImageState = state.mediaArtImageState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .padding(horizontal = 50.dp)
        )

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

@ComponentPreview
@Composable
fun HorizontalMusicPlayerViewPreview(
    @PreviewParameter(
        MusicPlayerViewStatePreviewParameterProvider::class,
        limit = 1
    ) state: MusicPlayerViewState
) {
    ThemedPreview {
        MusicPlayerView(
            state = state,
            playerViewMode = PlayerViewMode.BOTTOM_BAR,
            onProgressBarClicked = {},
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {},
            onToggleExpandedState = {}
        )
    }
}

@ComponentPreview
@Composable
fun VerticalMusicPlayerViewPreview(
    @PreviewParameter(
        MusicPlayerViewStatePreviewParameterProvider::class,
        limit = 1
    ) state: MusicPlayerViewState
) {
    ThemedPreview {
        MusicPlayerView(
            state = state,
            playerViewMode = PlayerViewMode.SIDE_BAR,
            onProgressBarClicked = {},
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {},
            onToggleExpandedState = {}
        )
    }
}