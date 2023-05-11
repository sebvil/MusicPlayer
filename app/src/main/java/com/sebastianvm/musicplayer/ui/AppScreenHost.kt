package com.sebastianvm.musicplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.player.MusicPlayerViewStatePreviewParameterProvider
import com.sebastianvm.musicplayer.ui.player.PlayerViewState
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@Composable
fun AppScreenHost(
    state: PlayerViewState?,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    content: @Composable () -> Unit
) {
    var height by remember {
        mutableStateOf(0f)
    }
    val density = LocalDensity.current
    val playerBottomPadding = 16.dp

    val paddingValues by remember {
        derivedStateOf {
            with(density) {
                PaddingValues(bottom = if (state == null) 0.dp else playerBottomPadding + height.toDp() + 8.dp)
            }
        }
    }
    CompositionLocalProvider(LocalPaddingValues provides paddingValues) {
        Box {
            content()
            state?.let {
                PlayerCard(
                    state = state,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 8.dp)
                        .padding(bottom = playerBottomPadding)
                        .onPlaced {
                            height = it.boundsInParent().height
                        },
                    onPreviousButtonClicked = onPreviousButtonClicked,
                    onNextButtonClicked = onNextButtonClicked,
                    onPlayToggled = onPlayToggled
                )
            }


        }
    }

}

@Composable
fun PlayerCard(
    state: PlayerViewState, modifier: Modifier = Modifier,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(8.dp)
        ) {
            MediaArtImage(
                mediaArtImageState = state.mediaArtImageState,
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.FillHeight
            )
            Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, top = 8.dp)) {
                Text(
                    text = state.trackInfoState.trackName,
                    modifier = Modifier.padding(bottom = 2.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = state.trackInfoState.artists,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onPreviousButtonClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prev),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            IconButton(onClick = onPlayToggled) {
                Icon(
                    painter = painterResource(id = state.playbackControlsState.playbackIcon.icon),
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
        LinearProgressIndicator(
            progress = state.playbackControlsState.trackProgressState.progress.percent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            trackColor = MaterialTheme.colorScheme.onPrimary

        )
    }
}


@ComponentPreview
@Composable
fun PlayerCardPreview(@PreviewParameter(MusicPlayerViewStatePreviewParameterProvider::class) state: PlayerViewState) {
    ThemedPreview {
        PlayerCard(
            state = state,
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {}
        )
    }
}
