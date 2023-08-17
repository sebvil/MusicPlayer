package com.sebastianvm.musicplayer.ui

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.player.PlayerViewState
import com.sebastianvm.musicplayer.ui.player.PlayerViewStatePreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreviews
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@Composable
fun AnimatedPlayerCard(
    state: PlayerViewState,
    transition: Transition<Boolean>,
    statusBarPadding: Dp,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    onDismissPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by transition.animateFloat(
        transitionSpec = transitionSpec(),
        label = "progress animation"
    ) { targetIsFullScreen ->
        if (targetIsFullScreen) 1f else 0f
    }

    val buttonSize by transition.animateDp(
        transitionSpec = transitionSpec(),
        label = "button size"
    ) { targetIsFullScreen ->
        if (targetIsFullScreen) 48.dp else 24.dp
    }

    val titleTextSize by transition.animateValue(
        typeConverter = TwoWayConverter(
            convertToVector = {
                AnimationVector1D(
                    it.value
                )
            },
            convertFromVector = {
                TextUnit(it.value, TextUnitType.Sp)
            }
        ),
        transitionSpec = transitionSpec(),
        label = "text style"
    ) { targetIsFullScreen ->
        if (targetIsFullScreen) MaterialTheme.typography.headlineSmall.fontSize else MaterialTheme.typography.titleSmall.fontSize
    }

    val artistTextSize by transition.animateValue(
        typeConverter = TwoWayConverter(
            convertToVector = {
                AnimationVector1D(
                    it.value
                )
            },
            convertFromVector = {
                TextUnit(it.value, TextUnitType.Sp)
            }
        ),
        transitionSpec = transitionSpec(),
        label = "text style"
    ) { targetIsFullScreen ->
        if (targetIsFullScreen) MaterialTheme.typography.titleLarge.fontSize else MaterialTheme.typography.bodyMedium.fontSize
    }

    ElevatedCard(modifier = modifier) {
        val fullScreenPadding = 26.dp
        val cardPadding = 12.dp
        val itemPadding = 8.dp
        MotionLayout(
            motionScene = MotionScene {
                val image = createRefFor("image")
                val text = createRefFor("text")
                val playbackControls = createRefFor("playbackControls")
                val progressBar = createRefFor("progressBar")
                val box = createRefFor("box")
                val hidePlayerButton = createRefFor("hidePlayer")
                val trackTime = createRefFor("trackTime")
                val fullScreenConstraints = constraintSet {
                    val chain = createVerticalChain(
                        image.withChainParams(bottomMargin = itemPadding),
                        text.withChainParams(topMargin = itemPadding, bottomMargin = itemPadding),
                        playbackControls.withChainParams(
                            topMargin = itemPadding,
                            bottomMargin = itemPadding
                        ),
                        progressBar.withChainParams(
                            topMargin = itemPadding,
                            bottomMargin = itemPadding
                        ),
                        trackTime.withChainParams(topMargin = itemPadding),
                        chainStyle = ChainStyle.Packed
                    )

                    constrain(hidePlayerButton) {
                        top.linkTo(parent.top, margin = statusBarPadding)
                        start.linkTo(parent.start, margin = fullScreenPadding)
                    }
                    constrain(box) {
                        height = Dimension.matchParent
                        width = Dimension.matchParent
                    }
                    constrain(chain) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    constrain(image) {
                        width = Dimension.fillToConstraints
                        start.linkTo(parent.start, margin = fullScreenPadding)
                        end.linkTo(parent.end, margin = fullScreenPadding)
                    }
                    constrain(text) {
                        start.linkTo(parent.start, margin = fullScreenPadding)
                        end.linkTo(parent.end, margin = fullScreenPadding)
                        width = Dimension.matchParent
                    }

                    constrain(playbackControls) {
                        start.linkTo(parent.start, margin = fullScreenPadding)
                        end.linkTo(parent.end, margin = fullScreenPadding)
                        width = Dimension.matchParent
                    }
                    constrain(progressBar) {
                        start.linkTo(parent.start, margin = fullScreenPadding)
                        end.linkTo(parent.end, margin = fullScreenPadding)
                        width = Dimension.matchParent
                    }
                    constrain(trackTime) {
                        start.linkTo(parent.start, margin = fullScreenPadding)
                        end.linkTo(parent.end, margin = fullScreenPadding)
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
                        playbackControls.withChainParams(),
                        progressBar.withChainParams(
                            topMargin = 2.dp,
                            bottomMargin = 2.dp
                        ),
                        chainStyle = ChainStyle.Packed
                    )

                    constrain(chain) {
                        top.linkTo(parent.top, margin = cardPadding)
                        bottom.linkTo(parent.bottom)
                    }

                    constrain(image) {
                        top.linkTo(text.top)
                        bottom.linkTo(text.bottom)
                        start.linkTo(parent.start, margin = cardPadding)
                        height = Dimension.fillToConstraints
                    }

                    constrain(text) {
                        start.linkTo(image.end, margin = itemPadding)
                        end.linkTo(parent.end, margin = cardPadding)
                        width = Dimension.fillToConstraints
                    }
                    constrain(playbackControls) {
                        end.linkTo(parent.end, margin = cardPadding)
                        start.linkTo(parent.start, margin = cardPadding)
                    }
                    constrain(progressBar) {
                        start.linkTo(parent.start, margin = cardPadding)
                        end.linkTo(parent.end, margin = cardPadding)
                        width = Dimension.matchParent
                    }
                }
                defaultTransition(from = regularConstraints, to = fullScreenConstraints)
            },
            progress = progress
        ) {
            if (progress != 0f) {
                IconButton(
                    onClick = onDismissPlayer,
                    modifier = Modifier
                        .layoutId("hidePlayer")
                        .alpha(progress)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.hide_player),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(progress)
                        .layoutId("trackTime")
                ) {
                    Text(
                        text = state.trackProgressState.currentPlaybackTime.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = state.trackProgressState.trackLength.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Box(modifier = Modifier.layoutId("box"))
            MediaArtImage(
                mediaArtImageState = state.mediaArtImageState,
                modifier = Modifier.layoutId("image")
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("text")
            ) {
                Text(
                    text = state.trackInfoState.trackName,
                    modifier = Modifier.padding(vertical = 2.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = titleTextSize,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = state.trackInfoState.artists,
                    modifier = Modifier.padding(vertical = 2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = artistTextSize,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .layoutId("playbackControls")
            ) {
                IconButton(onClick = onPreviousButtonClicked) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = stringResource(R.string.previous),
                        modifier = Modifier.size(buttonSize)
                    )
                }
                IconButton(onClick = onPlayToggled) {
                    Icon(
                        imageVector = state.playbackIcon.icon,
                        contentDescription = stringResource(R.string.previous),
                        modifier = Modifier.size(buttonSize)
                    )
                }
                IconButton(onClick = onNextButtonClicked) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = stringResource(R.string.previous),
                        modifier = Modifier.size(buttonSize)
                    )
                }
            }
            LinearProgressIndicator(
                progress = state.trackProgressState.progress.percent,
                modifier = Modifier
                    .layoutId("progressBar"),
                trackColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun PlayerCardPreview(
    @PreviewParameter(
        PlayerViewStatePreviewParameterProvider::class,
        limit = 1
    ) state: PlayerViewState
) {
    val transition = updateTransition(targetState = true, label = "player animation")
    ThemedPreview {
        AnimatedPlayerCard(
            state = state,
            statusBarPadding = 16.dp,
            transition = transition,
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {},
            onDismissPlayer = {}
        )
    }
}
