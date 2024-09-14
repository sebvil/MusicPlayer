@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.sebastianvm.musicplayer.features.player

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.sebastianvm.musicplayer.core.designsystems.components.ListItem
import com.sebastianvm.musicplayer.core.designsystems.components.MediaArtImage
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.designsystems.previews.PreviewComponents
import com.sebastianvm.musicplayer.core.designsystems.previews.ThemedPreview
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.kspannotations.MvvmComponent
import kotlin.time.Duration.Companion.milliseconds

@MvvmComponent(vmClass = PlayerViewModel::class)
@Composable
fun Player(state: PlayerState, handle: Handler<PlayerUserAction>, modifier: Modifier = Modifier) {
    when (state) {
        PlayerState.NotPlaying -> Unit
        is PlayerState.Playing -> {
            AnimatedPlayerCard(state = state, handle = handle, modifier = modifier)
        }
    }
}

@Composable
fun AnimatedPlayerCard(
    state: PlayerState.Playing,
    handle: Handler<PlayerUserAction>,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = state is PlayerState.QueueState) { handle(PlayerUserAction.DismissQueue) }

    Box(modifier = modifier) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    when (targetState) {
                        is PlayerState.FloatingState ->
                            (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                    scaleIn(
                                        initialScale = 0.92f,
                                        animationSpec = tween(220, delayMillis = 90),
                                    ))
                                .togetherWith(fadeOut(animationSpec = tween(90)))
                        is PlayerState.FullScreenState -> {
                            if (initialState is PlayerState.FloatingState) {
                                (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                        scaleIn(
                                            initialScale = 0.92f,
                                            animationSpec = tween(220, delayMillis = 90),
                                        ))
                                    .togetherWith(fadeOut(animationSpec = tween(90)))
                            } else {
                                (EnterTransition.None).togetherWith(
                                    slideOutOfContainer(
                                        towards =
                                            AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = tween(durationMillis = 220, 90),
                                    )
                                )
                            }
                        }
                        is PlayerState.QueueState -> {
                            (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                    slideIntoContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = tween(220, delayMillis = 90),
                                    ))
                                .togetherWith(ExitTransition.None)
                        }
                    }.apply {
                        targetContentZIndex =
                            when (targetState) {
                                is PlayerState.FloatingState -> 1f
                                is PlayerState.FullScreenState -> 2f
                                is PlayerState.QueueState -> 3f
                            }
                    }
                },
                label = "playerTransition",
                contentKey = { it::class },
            ) { playerState ->
                when (playerState) {
                    is PlayerState.FullScreenState -> {
                        FullScreenPlayer(
                            state = playerState,
                            handle = handle,
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            modifier =
                                Modifier.padding(
                                    top =
                                        WindowInsets.statusBars
                                            .asPaddingValues()
                                            .calculateTopPadding()
                                ),
                        )
                    }
                    is PlayerState.FloatingState -> {
                        FloatingPlayerCard(
                            state = playerState,
                            handle = handle,
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                        )
                    }
                    is PlayerState.QueueState -> {
                        PlayerWithQueue(
                            state = playerState,
                            handle = handle,
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            modifier = Modifier.padding(WindowInsets.systemBars.asPaddingValues()),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingPlayerCard(
    state: PlayerState.FloatingState,
    handle: Handler<PlayerUserAction>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        ElevatedCard(
            modifier =
                modifier.sharedBounds(
                    rememberSharedContentState(key = SharedContentStateKey.Container),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            colors =
                CardDefaults.elevatedCardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.playerContainerColor),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = state.trackInfoState.trackName,
                            modifier =
                                Modifier.sharedBounds(
                                    rememberSharedContentState(key = SharedContentStateKey.Title),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = state.trackInfoState.artists,
                            modifier =
                                Modifier.sharedBounds(
                                    rememberSharedContentState(key = SharedContentStateKey.Artist),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                    leadingContent = {
                        MediaArtImage(
                            artworkUri = state.artworkUri,
                            modifier =
                                Modifier.size(48.dp)
                                    .sharedElement(
                                        rememberSharedContentState(
                                            key = SharedContentStateKey.Image
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    ),
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { handle(PlayerUserAction.PlayToggled) }) {
                            Icon(
                                imageVector = state.playbackIcon.icon.icon(),
                                contentDescription =
                                    stringResource(state.playbackIcon.contentDescription),
                                modifier =
                                    Modifier.sharedElement(
                                        rememberSharedContentState(
                                            key = SharedContentStateKey.PlayPauseButton
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    ),
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )

                LinearProgressIndicator(
                    progress = { state.trackProgressState.progress.percent },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .sharedBounds(
                                rememberSharedContentState(key = SharedContentStateKey.ProgressBar),
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
            }
        }
    }
}

@Composable
private fun FullScreenPlayer(
    state: PlayerState.FullScreenState,
    handle: Handler<PlayerUserAction>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        Column(
            modifier =
                Modifier.background(color = MaterialTheme.colorScheme.playerContainerColor)
                    .then(modifier)
                    .padding(horizontal = 16.dp)
                    .sharedBounds(
                        rememberSharedContentState(key = SharedContentStateKey.Container),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = { handle(PlayerUserAction.DismissFullScreenPlayer) }) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(RString.hide_player),
                        modifier = Modifier.size(48.dp),
                    )
                }
                IconButton(onClick = { handle(PlayerUserAction.QueueTapped) }) {
                    Icon(
                        imageVector = AppIcons.QueueMusic.icon(),
                        contentDescription = stringResource(RString.hide_player),
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            val imageMaxSize = 350.dp
            MediaArtImage(
                artworkUri = state.artworkUri,
                modifier =
                    Modifier.sizeIn(maxHeight = imageMaxSize, maxWidth = imageMaxSize)
                        .fillMaxWidth()
                        .sharedElement(
                            rememberSharedContentState(key = SharedContentStateKey.Image),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                contentScale = ContentScale.Fit,
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = state.trackInfoState.trackName,
                        modifier =
                            Modifier.sharedBounds(
                                rememberSharedContentState(key = SharedContentStateKey.Title),
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    )
                },
                supportingContent = {
                    Text(
                        text = state.trackInfoState.artists,
                        modifier =
                            Modifier.sharedBounds(
                                rememberSharedContentState(key = SharedContentStateKey.Artist),
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = { handle(PlayerUserAction.PreviousButtonClicked) }) {
                    Icon(
                        imageVector = AppIcons.SkipPrevious.icon(),
                        contentDescription = stringResource(RString.previous),
                        modifier =
                            Modifier.size(48.dp)
                                .sharedElement(
                                    rememberSharedContentState(
                                        key = SharedContentStateKey.PreviousButton
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                ),
                    )
                }
                IconButton(onClick = { handle(PlayerUserAction.PlayToggled) }) {
                    Icon(
                        imageVector = state.playbackIcon.icon.icon(),
                        contentDescription = stringResource(state.playbackIcon.contentDescription),
                        modifier =
                            Modifier.size(48.dp)
                                .sharedElement(
                                    rememberSharedContentState(
                                        key = SharedContentStateKey.PlayPauseButton
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                ),
                    )
                }
                IconButton(onClick = { handle(PlayerUserAction.NextButtonClicked) }) {
                    Icon(
                        imageVector = AppIcons.SkipNext.icon(),
                        contentDescription = stringResource(RString.previous),
                        modifier =
                            Modifier.size(48.dp)
                                .sharedElement(
                                    rememberSharedContentState(
                                        key = SharedContentStateKey.NextButton
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                ),
                    )
                }
            }

            ProgressSlider(
                trackProgressState = state.trackProgressState,
                onProgressBarValueChange = { position ->
                    handle(
                        PlayerUserAction.ProgressBarClicked(
                            position = position,
                            trackLength = state.trackProgressState.trackLength,
                        )
                    )
                },
                modifier =
                    Modifier.fillMaxWidth()
                        .sharedElement(
                            rememberSharedContentState(key = SharedContentStateKey.ProgressBar),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PlayerWithQueue(
    state: PlayerState.QueueState,
    handle: Handler<PlayerUserAction>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surface).then(modifier)
        ) {
            IconButton(onClick = { handle(PlayerUserAction.DismissQueue) }) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = stringResource(RString.hide_player),
                    modifier = Modifier.size(48.dp),
                )
            }

            state.queueMvvmComponent.Content(modifier = Modifier.weight(1f).zIndex(0f))

            Column(modifier = Modifier.fillMaxWidth().zIndex(1f)) {
                LinearProgressIndicator(
                    progress = { state.trackProgressState.progress.percent },
                    modifier =
                        Modifier.fillMaxWidth()
                            .sharedBounds(
                                rememberSharedContentState(key = SharedContentStateKey.ProgressBar),
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                ) {
                    IconButton(onClick = { handle(PlayerUserAction.PreviousButtonClicked) }) {
                        Icon(
                            imageVector = AppIcons.SkipPrevious.icon(),
                            contentDescription = stringResource(RString.previous),
                            modifier =
                                Modifier.size(48.dp)
                                    .sharedElement(
                                        rememberSharedContentState(
                                            key = SharedContentStateKey.PreviousButton
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    ),
                        )
                    }
                    IconButton(onClick = { handle(PlayerUserAction.PlayToggled) }) {
                        Icon(
                            imageVector = state.playbackIcon.icon.icon(),
                            contentDescription =
                                stringResource(state.playbackIcon.contentDescription),
                            modifier =
                                Modifier.size(48.dp)
                                    .sharedElement(
                                        rememberSharedContentState(
                                            key = SharedContentStateKey.PlayPauseButton
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    ),
                        )
                    }
                    IconButton(onClick = { handle(PlayerUserAction.NextButtonClicked) }) {
                        Icon(
                            imageVector = AppIcons.SkipNext.icon(),
                            contentDescription = stringResource(RString.previous),
                            modifier =
                                Modifier.size(48.dp)
                                    .sharedElement(
                                        rememberSharedContentState(
                                            key = SharedContentStateKey.NextButton
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    ),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressSlider(
    trackProgressState: TrackProgressState,
    onProgressBarValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = SliderDefaults.colors(activeTrackColor = MaterialTheme.colorScheme.primary)
    val interactions = remember { mutableStateListOf<Interaction>() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }

    val progress = trackProgressState.progress.percent
    var manualSliderPosition by remember { mutableFloatStateOf(progress) }
    val sliderPosition by
        remember(progress, interactions) {
            derivedStateOf {
                if (interactions.isEmpty()) {
                    progress
                } else {
                    manualSliderPosition
                }
            }
        }

    Column(modifier = modifier) {
        Slider(
            value = sliderPosition,
            onValueChange = { manualSliderPosition = it },
            valueRange = 0f..1f,
            onValueChangeFinished = {
                onProgressBarValueChange((manualSliderPosition * Percentage.MAX).toInt())
            },
            modifier = Modifier.fillMaxWidth(),
            colors = colors,
            thumb = {
                val size by
                    animateDpAsState(
                        if (interactions.isNotEmpty()) {
                            PROGRESS_BAR_THUMB_SIZE_LARGE
                        } else {
                            PROGRESS_BAR_THUMB_SIZE_SMALL
                        },
                        label = "size",
                    )
                val offset = (-4 * size / 8 + 8.dp).coerceAtLeast(0.dp)

                Icon(
                    imageVector = AppIcons.Circle.icon(),
                    contentDescription = null,
                    modifier =
                        Modifier.layout { measurable, _ ->
                            val placeable =
                                measurable.measure(
                                    Constraints.fixed(
                                        width = size.roundToPx(),
                                        height = size.roundToPx(),
                                    )
                                )
                            layout(placeable.width, placeable.height) {
                                placeable.place(x = 0, y = offset.roundToPx())
                            }
                        },
                    tint = colors.thumbColor,
                )
            },
            track = {
                LinearProgressIndicator(
                    progress = { sliderPosition },
                    gapSize = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                    drawStopIndicator = {},
                )
            },
            interactionSource = interactionSource,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text =
                    (sliderPosition * trackProgressState.trackLength.inWholeMilliseconds)
                        .toLong()
                        .milliseconds
                        .toDisplayableString()
            )
            Text(text = trackProgressState.trackLength.toDisplayableString())
        }
    }
}

private val PROGRESS_BAR_THUMB_SIZE_LARGE = 16.dp
private val PROGRESS_BAR_THUMB_SIZE_SMALL = 10.dp

@PreviewComponents
@Composable
private fun PlayerCardPreview(
    @PreviewParameter(PlayerStatePreviewParameterProvider::class, limit = 1)
    state: PlayerState.Playing
) {
    ThemedPreview { AnimatedPlayerCard(state = state, handle = {}) }
}

private enum class SharedContentStateKey {
    Container,
    Image,
    Title,
    Artist,
    PreviousButton,
    NextButton,
    PlayPauseButton,
    ProgressBar,
}

val ColorScheme.playerContainerColor
    get() = this.surfaceContainerHigh
