package com.sebastianvm.musicplayer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AnimatedTextOverflow
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.player.PlayerViewState

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
    val playerBottomPadding = 20.dp


    var isFullScreen by remember {
        mutableStateOf(false)
    }

    BackHandler(enabled = isFullScreen) {
        if (isFullScreen) {
            isFullScreen = false
        }
    }
    val transition =
        updateTransition(targetState = isFullScreen, label = "player animation")
    val progress by transition.animateFloat(label = "progress animation") { targetIsFullScreen ->
        if (targetIsFullScreen) 1f else 0f
    }
    val paddingHorizontal by transition.animateDp(label = "padding horizontal") { targetIsFullScreen ->
        if (targetIsFullScreen) 0.dp else 8.dp
    }

    val paddingBottom by transition.animateDp(label = "padding bottom") { targetIsFullScreen ->
        if (targetIsFullScreen) 0.dp else playerBottomPadding
    }
    val paddingValues by remember {
        derivedStateOf {
            with(density) {
                PaddingValues(bottom = paddingBottom + height.toDp() + 8.dp)
            }
        }
    }
    CompositionLocalProvider(LocalPaddingValues provides paddingValues) {
        Box {
            content()
            state?.let {
                AnimatedPlayerCard(
                    state = state,
                    progress = progress,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = paddingBottom)
                        .padding(horizontal = paddingHorizontal)
                        .onPlaced {
                            height = it.boundsInParent().height
                        }
                        .clickable(enabled = !isFullScreen) {
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


@OptIn(ExperimentalMotionApi::class)
@Composable
fun AnimatedPlayerCard(
    state: PlayerViewState,
    progress: Float,
    modifier: Modifier = Modifier,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
) {
    Card(modifier = modifier) {
        val padding = 12.dp
        MotionLayout(
            motionScene = MotionScene {
                val image = createRefFor("image")
                val text = createRefFor("text")
                val playbackControls = createRefFor("playbackControls")
                val progressBar = createRefFor("progressBar")
                val box = createRefFor("box")
                val fullScreenConstraints = constraintSet {
                    val chain = createVerticalChain(
                        image,
                        text,
                        playbackControls,
                        progressBar,
                        chainStyle = ChainStyle.Packed
                    )
                    constrain(box) {
                        height = Dimension.matchParent
                        width = Dimension.matchParent
                    }
                    constrain(chain) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    constrain(image) {
                        width = Dimension.preferredValue(250.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    constrain(text) {
                        start.linkTo(parent.start, margin = padding)
                        end.linkTo(parent.end, margin = padding)
                        width = Dimension.matchParent
                    }

                    constrain(playbackControls) {
                        start.linkTo(parent.start, margin = padding)
                        end.linkTo(parent.end, margin = padding)
                        width = Dimension.matchParent
                    }
                    constrain(progressBar) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, margin = padding)
                        end.linkTo(parent.end, margin = padding)
                        width = Dimension.matchParent
                    }
                }
                val regularConstraints = constraintSet {
                    constrain(box) {
                        height = Dimension.value(0.dp)
                        width = Dimension.value(0.dp)
                    }
                    val chain = createVerticalChain(
                        text,
                        playbackControls,
                        progressBar,
                        chainStyle = ChainStyle.Packed
                    )

                    constrain(chain) {
                        top.linkTo(parent.top, margin = padding)
                        bottom.linkTo(parent.bottom)
                    }

                    constrain(image) {
                        top.linkTo(text.top)
                        bottom.linkTo(text.bottom)
                        start.linkTo(parent.start, margin = padding)
                        end.linkTo(text.start)
                        height = Dimension.fillToConstraints
                    }


                    constrain(text) {
                        start.linkTo(image.end, margin = padding)
                    }
                    constrain(playbackControls) {
                        start.linkTo(parent.start, margin = padding)
                        end.linkTo(parent.end, margin = padding)
                        width = Dimension.matchParent
                    }
                    constrain(progressBar) {
                        start.linkTo(parent.start, margin = padding)
                        end.linkTo(parent.end, margin = padding)
                        width = Dimension.matchParent
                    }

                }
                defaultTransition(from = regularConstraints, to = fullScreenConstraints)
            },
            progress = progress,
        ) {
            Box(modifier = Modifier.layoutId("box"))
            MediaArtImage(
                mediaArtImageState = state.mediaArtImageState,
                modifier = Modifier.layoutId("image"),
            )
            Column(
                modifier = Modifier.layoutId("text")
            ) {
                AnimatedTextOverflow(
                    text = state.trackInfoState.trackName,
                    modifier = Modifier.padding(bottom = 2.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
                AnimatedTextOverflow(
                    text = state.trackInfoState.artists,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .layoutId("playbackControls")
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
                    .layoutId("progressBar"),
                trackColor = MaterialTheme.colorScheme.onPrimary

            )
        }
    }

}


//@ComponentPreview
//@Composable
//fun PlayerCardPreview(
//    @PreviewParameter(
//        MusicPlayerViewStatePreviewParameterProvider::class,
//        limit = 2
//    ) state: PlayerViewState
//) {
//    ThemedPreview {
//        AnimatedPlayerCard(
//            state = state,
//            isFullScreen = false,
//            onPreviousButtonClicked = {},
//            onNextButtonClicked = {},
//            onPlayToggled = {}
//        )
//    }
//}
