package com.sebastianvm.musicplayer.ui.player

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
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
import com.sebastianvm.musicplayer.ui.util.compose.BooleanPreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComposePreviews
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


@Composable
fun MusicPlayerScreen(
    screenViewModel: MusicPlayerViewModel = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) { state ->
        MusicPlayerLayout(state = state, playerDelegate = object : PlayerDelegate() {
            override fun togglePlay() {
                screenViewModel.onPlayToggled()
            }

            override fun nextClicked() {
                screenViewModel.onNextTapped()
            }

            override fun previousClicked() {
                screenViewModel.onPreviousTapped()
            }

            override fun onProgressBarClicked(position: Int) {
                screenViewModel.onProgressTapped(position)
            }
        })
    }
}

@ComposePreviews
@Composable
fun MusicPlayerScreenPreview(@PreviewParameter(MusicPlayerStatePreviewParameterProvider::class) state: MusicPlayerState) {
    ScreenPreview {
        MusicPlayerLayout(state = state, playerDelegate = PlayerDelegate())
    }
}

@Composable
fun MusicPlayerLayout(
    state: MusicPlayerState,
    playerDelegate: PlayerDelegate
) {
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
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(all = AppDimensions.spacing.mediumLarge),
                contentScale = ContentScale.FillHeight
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                TrackInfo(TrackInfoState(state.trackName ?: "", state.artists ?: ""))
                TrackProgress(
                    TrackProgressState(
                        trackLengthMs = state.trackLengthMs,
                        currentPlaybackTimeMs = state.currentPlaybackTimeMs
                    ),
                    delegate = playerDelegate
                )
                MediaButtons(state.isPlaying, playerDelegate)
            }
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
            TrackInfo(TrackInfoState(state.trackName ?: "", state.artists ?: ""))
            TrackProgress(
                TrackProgressState(
                    trackLengthMs = state.trackLengthMs,
                    currentPlaybackTimeMs = state.currentPlaybackTimeMs
                ),
                delegate = playerDelegate
            )
            MediaButtons(state.isPlaying, playerDelegate)

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


data class TrackProgressState(
    val trackLengthMs: Long?,
    val currentPlaybackTimeMs: Long?
)

data class MinutesSecondsTime(val minutes: Long, val seconds: Long) {
    companion object {
        fun fromMs(ms: Long): MinutesSecondsTime {
            return MinutesSecondsTime((ms / 1000) / 60, (ms / 1000) % 60)
        }
    }
}

interface TrackProgressDelegate {
    fun onProgressBarClicked(position: Int) = Unit
}

@Preview
@Composable
fun TrackProgress(
    @PreviewParameter(TrackProgressStatePreviewParameterProvider::class) trackProgressState: TrackProgressState,
    delegate: TrackProgressDelegate = object : TrackProgressDelegate {}
) {
    with(trackProgressState) {
        val progress =
            if (currentPlaybackTimeMs == null || trackLengthMs == null || trackLengthMs == 0L) {
                0f
            } else {
                currentPlaybackTimeMs.toFloat() / trackLengthMs.toFloat()
            }

        val currentDuration = MinutesSecondsTime.fromMs(currentPlaybackTimeMs ?: 0)
        val trackDuration = MinutesSecondsTime.fromMs(trackLengthMs ?: 0)
        Column(modifier = Modifier.padding(all = AppDimensions.spacing.medium)) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppDimensions.spacing.xSmall)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val xOffset = offset.x
                            val percentPosition = (xOffset / size.width) * 100
                            delegate.onProgressBarClicked(percentPosition.toInt())
                        }
                    }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "%02d:%02d".format(currentDuration.minutes, currentDuration.seconds),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "%02d:%02d".format(trackDuration.minutes, trackDuration.seconds),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }


}

open class PlayerDelegate : TrackProgressDelegate {
    open fun togglePlay() = Unit
    open fun nextClicked() = Unit
    open fun previousClicked() = Unit
}

@Preview
@Composable
fun MediaButtons(
    @PreviewParameter(BooleanPreviewParameterProvider::class) isPlaying: Boolean,
    delegate: PlayerDelegate = PlayerDelegate()
) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { delegate.previousClicked() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_prev),
                contentDescription = stringResource(R.string.previous),
            )
        }
        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        IconButton(onClick = { delegate.togglePlay() }) {
            Icon(
                painter = painterResource(id = playPauseIcon),
                contentDescription = stringResource(R.string.previous),
            )
        }
        IconButton(onClick = { delegate.nextClicked() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = stringResource(R.string.previous),
            )
        }
    }
}
