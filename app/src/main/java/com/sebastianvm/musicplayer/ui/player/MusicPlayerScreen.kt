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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AnimatedTextOverflow
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface


@Composable
fun MusicPlayerScreen(
    screenViewModel: MusicPlayerViewModel = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) {
        MusicPlayerLayout(screenViewModel)
    }
}

@ScreenPreview
@Composable
fun MusicPlayerScreenPreview(@PreviewParameter(MusicPlayerStatePreviewParameterProvider::class) state: MusicPlayerState) {
    ScreenPreview {
        MusicPlayerLayout(viewModel = DefaultViewModelInterfaceProvider.getDefaultInstance(state))
    }
}

@Composable
fun MusicPlayerLayout(viewModel: ViewModelInterface<MusicPlayerState, MusicPlayerUserAction>) {
    val state by viewModel.state.collectAsState()
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row {
            MediaArtImage(
                uri = state.trackArt,
                contentDescription = stringResource(
                    id = R.string.album_art_for_album,
                    state.trackName ?: ""
                ),
                backupResource = R.drawable.ic_album,
                backupContentDescription = R.string.placeholder_album_art,
                contentScale = ContentScale.FillHeight
            )
            PlaybackInfoAndButtons(viewModel, modifier = Modifier.fillMaxHeight())
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
                backupResource = R.drawable.ic_album,
                backupContentDescription = R.string.placeholder_album_art,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(all = AppDimensions.spacing.mediumLarge),
                contentScale = ContentScale.FillHeight
            )
            PlaybackInfoAndButtons(viewModel)

        }
    }
}

data class TrackInfoState(val trackName: String, val artists: String)

@Preview
@Composable
fun TrackInfo(@PreviewParameter(TrackInfoStatePreviewParameterProvider::class) state: TrackInfoState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.spacing.mediumLarge)
    ) {
        AnimatedTextOverflow(
            text = state.trackName,
            style = MaterialTheme.typography.titleLarge,
        )
        AnimatedTextOverflow(
            text = state.artists,
            modifier = Modifier.alpha(0.6f),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}


@Composable
fun PlaybackInfoAndButtons(
    viewModel: ViewModelInterface<MusicPlayerState, MusicPlayerUserAction>,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        TrackInfo(TrackInfoState(state.trackName ?: "", state.artists ?: ""))
        TrackProgress(
            TrackProgressState(
                trackLengthMs = state.trackLengthMs,
                currentPlaybackTimeMs = state.currentPlaybackTimeMs
            ),
            onProgressBarClicked = { position ->
                viewModel.handle(
                    MusicPlayerUserAction.ProgressBarClicked(
                        position
                    )
                )
            }
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { viewModel.handle(MusicPlayerUserAction.PreviousButtonClicked) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prev),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            val playPauseIcon = if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            IconButton(onClick = { viewModel.handle(MusicPlayerUserAction.PlayToggled) }) {
                Icon(
                    painter = painterResource(id = playPauseIcon),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            IconButton(onClick = { viewModel.handle(MusicPlayerUserAction.NextButtonClicked) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = stringResource(R.string.previous),
                )
            }
        }
    }
}
