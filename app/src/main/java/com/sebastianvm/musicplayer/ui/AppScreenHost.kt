package com.sebastianvm.musicplayer.ui

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.player.MusicPlayerViewStatePreviewParameterProvider
import com.sebastianvm.musicplayer.ui.player.PlayerViewState
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@OptIn(ExperimentalAnimationApi::class)
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

    var isFullScreen by remember {
        mutableStateOf(false)
    }
    CompositionLocalProvider(LocalPaddingValues provides paddingValues) {
        Box {
            content()
            state?.let {
                AnimatedPlayerCard(
                    state = state,
                    isFullScreen = isFullScreen,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .onPlaced {
                            height = it.boundsInParent().height
                        }
                        .clickable {
                            isFullScreen = !isFullScreen
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
fun VerticalPlayerLayout(
    state: PlayerViewState,
    modifier: Modifier = Modifier,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit
) {
    Column(modifier = Modifier.padding(all = 16.dp), verticalArrangement = Arrangement.Center) {
        MediaArtImage(
            mediaArtImageState = state.mediaArtImageState,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, top = 8.dp)) {
            Text(
                text = state.trackInfoState.trackName,
                modifier = Modifier.padding(bottom = 2.dp),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = state.trackInfoState.artists,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
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

@Composable
fun HorizontalPlayerLayout(
    state: PlayerViewState,
    modifier: Modifier = Modifier,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
) {
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
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = state.trackInfoState.artists,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
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

@Composable
fun AnimatedPlayerCard(
    state: PlayerViewState,
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
) {
    val fullModifier = modifier.animateContentSize().run {
        if (isFullScreen) {
            fillMaxSize()
        } else {
            fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 16.dp)
        }
    }
    Card(modifier = fullModifier) {
        val imageModifier = Modifier.animateContentSize() { init, final ->
            Log.i("ANIM", "$init, $final")
        }.run {
            if (isFullScreen) fillMaxWidth() else this
        }

        ConstraintLayout(modifier = if (isFullScreen) Modifier.fillMaxHeight() else Modifier) {
            val (image, text, playbackControls, progressBar) = createRefs()
            MediaArtImage(
                mediaArtImageState = state.mediaArtImageState,
                modifier = imageModifier.constrainAs(image) {
                    if (isFullScreen) {
                        top.linkTo(parent.top)
                        bottom.linkTo(text.top)
                        width = Dimension.matchParent

                    } else {
                        top.linkTo(text.top)
                        bottom.linkTo(text.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(text.start)
                        width = Dimension.wrapContent
                        height = Dimension.fillToConstraints
                    }
                },
                contentScale = if (isFullScreen) ContentScale.FillWidth else ContentScale.FillHeight
            )
            Column(
                modifier = Modifier
                    .constrainAs(text) {
                        if (isFullScreen) {
                            top.linkTo(image.bottom)
                            bottom.linkTo(playbackControls.top)
                        } else {
                            top.linkTo(parent.top)
                            bottom.linkTo(playbackControls.top)
                            start.linkTo(image.end)
                        }
                    }) {
                Text(
                    text = state.trackInfoState.trackName,
                    modifier = Modifier.padding(bottom = 2.dp),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = state.trackInfoState.artists,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(playbackControls) {
                        top.linkTo(text.bottom)
                        bottom.linkTo(progressBar.top)
                    }
            ) {
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
                    .padding(horizontal = 8.dp)
                    .constrainAs(progressBar) {
                        top.linkTo(playbackControls.bottom)
                        bottom.linkTo(parent.bottom)
                    },
                trackColor = MaterialTheme.colorScheme.onPrimary

            )
        }

    }
//        if (isFullScreen) {
//            VerticalPlayerLayout(
//                state = state,
//                onPreviousButtonClicked = onPreviousButtonClicked,
//                onNextButtonClicked = onNextButtonClicked,
//                onPlayToggled = onPlayToggled
//            )
//        } else {
//            HorizontalPlayerLayout(
//                state = state,
//                onPreviousButtonClicked = onPreviousButtonClicked,
//                onNextButtonClicked = onNextButtonClicked,
//                onPlayToggled = onPlayToggled
//            )
//        }


}


@ComponentPreview
@Composable
fun PlayerCardPreview(
    @PreviewParameter(
        MusicPlayerViewStatePreviewParameterProvider::class,
        limit = 2
    ) state: PlayerViewState
) {
    ThemedPreview {
        AnimatedPlayerCard(
            state = state,
            isFullScreen = false,
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {}
        )
    }
}
