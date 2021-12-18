package com.sebastianvm.musicplayer.ui.player

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.BooleanPreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


@Composable
fun MusicPlayerScreen(
    screenViewModel: MusicPlayerViewModel = viewModel(),
) {
    Screen(screenViewModel = screenViewModel, eventHandler = {}) { state ->
        MusicPlayerLayout(state = state, mediaButtonsDelegate = object : MediaButtonsDelegate() {
            override fun togglePlay() {
                screenViewModel.handle(MusicPlayerUserAction.TogglePlay)
            }

            override fun nextClicked() {
                screenViewModel.handle(MusicPlayerUserAction.NextTapped)
            }

            override fun previousClicked() {
                screenViewModel.handle(MusicPlayerUserAction.PreviousTapped)
            }
        })
    }
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MusicPlayerScreenPreview(@PreviewParameter(MusicPlayerStatePreviewParameterProvider::class) state: MusicPlayerState) {
    ScreenPreview {
        MusicPlayerLayout(state = state, mediaButtonsDelegate = MediaButtonsDelegate())
    }
}

@Composable
fun MusicPlayerLayout(
    state: MusicPlayerState,
    mediaButtonsDelegate: MediaButtonsDelegate
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row {
            MediaArtImage(
                image = state.trackArt,
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
                    )
                )
                MediaButtons(state.isPlaying, mediaButtonsDelegate)
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            MediaArtImage(
                image = state.trackArt,
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
                )
            )
            MediaButtons(state.isPlaying, mediaButtonsDelegate)

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

@Preview
@Composable
fun TrackProgress(@PreviewParameter(TrackProgressStatePreviewParameterProvider::class) trackProgressState: TrackProgressState) {
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

open class MediaButtonsDelegate {
    open fun togglePlay() = Unit
    open fun nextClicked() = Unit
    open fun previousClicked() = Unit
}

@Preview
@Composable
fun MediaButtons(
    @PreviewParameter(BooleanPreviewParameterProvider::class) isPlaying: Boolean,
    delegate: MediaButtonsDelegate = MediaButtonsDelegate()
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
