package com.sebastianvm.musicplayer.ui.player

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun MusicPlayerScreen(viewModel: MusicPlayerViewModel, navigationDelegate: NavigationDelegate) {
    Screen(
        screenViewModel = viewModel,
        navigationDelegate = navigationDelegate
    ) { state, delegate ->
        MusicPlayerScreen(
            state = state,
            screenDelegate = delegate
        )
    }
}

@Composable
fun MusicPlayerScreen(
    state: MusicPlayerState,
    screenDelegate: ScreenDelegate<MusicPlayerUserAction>
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            MediaArtImage(
                uri = state.trackArt,
                contentDescription = stringResource(
                    id = R.string.album_art_for_album,
                    state.trackName ?: ""
                ),
                backupImage = painterResource(id = R.drawable.ic_album),
                backupContentDescription = stringResource(id = R.string.placeholder_album_art),
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(all = AppDimensions.spacing.mediumLarge),
                contentScale = ContentScale.FillWidth
            )
            PlaybackInfoAndButtons(state, screenDelegate, modifier = Modifier.fillMaxHeight())
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            MediaArtImage(
                uri = state.trackArt,
                contentDescription = stringResource(
                    id = R.string.album_art_for_album,
                    state.trackName ?: ""
                ),
                backupImage = painterResource(id = R.drawable.ic_album),
                backupContentDescription = stringResource(id = R.string.placeholder_album_art),
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(all = AppDimensions.spacing.mediumLarge),
                contentScale = ContentScale.FillHeight
            )
            PlaybackInfoAndButtons(state, screenDelegate)

        }
    }
}


@Composable
fun PlaybackInfoAndButtons(
    state: MusicPlayerState,
    screenDelegate: ScreenDelegate<MusicPlayerUserAction>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        TrackInfo(TrackInfoState(state.trackName ?: "", state.artists ?: ""))
        TrackProgress(
            OldTrackProgressState(
                trackLengthMs = state.trackLengthMs,
                currentPlaybackTimeMs = state.currentPlaybackTimeMs
            ),
            onProgressBarClicked = { position ->
                screenDelegate.handle(
                    MusicPlayerUserAction.ProgressBarClicked(
                        position
                    )
                )
            }
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { screenDelegate.handle(MusicPlayerUserAction.PreviousButtonClicked) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prev),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            val playPauseIcon = if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            IconButton(onClick = { screenDelegate.handle(MusicPlayerUserAction.PlayToggled) }) {
                Icon(
                    painter = painterResource(id = playPauseIcon),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            IconButton(onClick = { screenDelegate.handle(MusicPlayerUserAction.NextButtonClicked) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = stringResource(R.string.previous),
                )
            }
        }
    }
}
