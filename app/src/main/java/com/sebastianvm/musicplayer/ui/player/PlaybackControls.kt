package com.sebastianvm.musicplayer.ui.player

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R

enum class PlaybackIcon(@DrawableRes val icon: Int) {
    PLAY(icon = R.drawable.ic_play),
    PAUSE(icon = R.drawable.ic_pause)
}


data class PlaybackControlsState(
    val trackProgressState: TrackProgressState,
    val playbackIcon: PlaybackIcon
)

@Composable
fun PlaybackControls(
    state: PlaybackControlsState,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        TrackProgress(
            state = state.trackProgressState,
            onProgressBarClicked = onProgressBarClicked
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onPreviousButtonClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prev),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            IconButton(onClick = onPlayToggled) {
                Icon(
                    painter = painterResource(id = state.playbackIcon.icon),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            IconButton(onClick = onNextButtonClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = stringResource(R.string.previous),
                )
            }
        }
    }
}