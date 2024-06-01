@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sebastianvm.musicplayer.features.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.util.compose.PreviewComponents
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState
import com.sebastianvm.musicplayer.ui.util.toDisplayableString
import kotlinx.coroutines.flow.Flow

class PlayerUiComponent(
    private val delegate: PlayerDelegate,
    private val props: Flow<PlayerProps>
) : BaseUiComponent<NoArguments, PlayerStateHolder>() {
    override val arguments: NoArguments = NoArguments

    override fun createStateHolder(dependencies: DependencyContainer): PlayerStateHolder {
        return getPlayerStateHolder(dependencies = dependencies, delegate = delegate, props = props)
    }

    @Composable
    override fun Content(stateHolder: PlayerStateHolder, modifier: Modifier) {
        Player(stateHolder = stateHolder, modifier = modifier)
    }
}

@Composable
fun Player(stateHolder: PlayerStateHolder, modifier: Modifier = Modifier) {
    val state by stateHolder.currentState
    when (val currentState = state) {
        PlayerState.NotPlaying -> Unit
        is PlayerState.Playing -> {
            AnimatedPlayerCard(
                state = currentState,
                handle = stateHolder::handle,
                modifier = modifier
            )
        }
    }
}

@Composable
fun AnimatedPlayerCard(
    state: PlayerState.Playing,
    handle: Handler<PlayerUserAction>,
    modifier: Modifier = Modifier
) {
    SharedTransitionLayout(modifier = modifier) {
        AnimatedContent(
            targetState = state.isFullscreen,
            label = "playerTransition"
        ) { isFullScreen ->
            if (isFullScreen) {
                FullScreenPlayer(
                    state = state,
                    handle = handle,
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            } else {
                FloatingPlayerCard(
                    state = state,
                    handle = handle,
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout,
                )
            }
        }
    }
}

@Composable
private fun FloatingPlayerCard(
    state: PlayerState.Playing,
    handle: Handler<PlayerUserAction>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        ElevatedCard(
            modifier = modifier.sharedBounds(
                rememberSharedContentState(
                    key = CONTAINER_SHARED_TRANSITION_KEY
                ),
                animatedVisibilityScope = animatedVisibilityScope,
            ),
            colors = CardDefaults.elevatedCardColors()
                .copy(containerColor = MaterialTheme.colorScheme.playerContainerColor)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = state.trackInfoState.trackName,
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState(
                                        key = TITLE_SHARED_TRANSITION_KEY
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    supportingContent = {
                        Text(
                            text = state.trackInfoState.artists,
                            modifier = Modifier.sharedBounds(
                                rememberSharedContentState(
                                    key = ARTIST_SHARED_TRANSITION_KEY
                                ),
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    leadingContent = {
                        MediaArtImage(
                            mediaArtImageState = state.mediaArtImageState,
                            modifier = Modifier
                                .size(48.dp)
                                .sharedElement(
                                    rememberSharedContentState(
                                        key = IMAGE_SHARED_TRANSITION_KEY
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                )
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sharedElement(
                            rememberSharedContentState(
                                key = BUTTONS_SHARED_TRANSITION_KEY
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                ) {
                    IconButton(onClick = {
                        handle(PlayerUserAction.PreviousButtonClicked)
                    }) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = stringResource(R.string.previous),
                        )
                    }
                    IconButton(onClick = {
                        handle(PlayerUserAction.PlayToggled)
                    }) {
                        Icon(
                            imageVector = state.playbackIcon.icon,
                            contentDescription = stringResource(state.playbackIcon.contentDescription),
                        )
                    }
                    IconButton(onClick = {
                        handle(PlayerUserAction.NextButtonClicked)
                    }) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = stringResource(R.string.previous),
                        )
                    }
                }

                ProgressSlider(
                    progress = state.trackProgressState.progress.percent,
                    onProgressBarValueChange = { position ->
                        handle(
                            PlayerUserAction.ProgressBarClicked(
                                position = position,
                                trackLength = state.trackProgressState.trackLength
                            )
                        )
                    },
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(
                            key = PROGRESS_BAR_SHARED_TRANSITION_KEY
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                )
            }
        }
    }
}

@Composable
private fun FullScreenPlayer(
    state: PlayerState.Playing,
    handle: Handler<PlayerUserAction>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.playerContainerColor)
                .padding(horizontal = 16.dp)
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding()
                )
                .sharedBounds(
                    rememberSharedContentState(
                        key = CONTAINER_SHARED_TRANSITION_KEY
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    handle(PlayerUserAction.DismissFullScreenPlayer)
                },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.hide_player),
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            val imageMaxSize = 350.dp
            MediaArtImage(
                mediaArtImageState = state.mediaArtImageState,
                modifier = Modifier
                    .sizeIn(maxHeight = imageMaxSize, maxWidth = imageMaxSize)
                    .fillMaxWidth()
                    .sharedElement(
                        rememberSharedContentState(
                            key = IMAGE_SHARED_TRANSITION_KEY
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
                contentScale = ContentScale.Fit
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = state.trackInfoState.trackName,
                        modifier = Modifier.sharedBounds(
                            rememberSharedContentState(
                                key = TITLE_SHARED_TRANSITION_KEY
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                supportingContent = {
                    Text(
                        text = state.trackInfoState.artists,
                        modifier = Modifier.sharedBounds(
                            rememberSharedContentState(
                                key = ARTIST_SHARED_TRANSITION_KEY
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)

            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedElement(
                        rememberSharedContentState(
                            key = BUTTONS_SHARED_TRANSITION_KEY
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
            ) {
                IconButton(onClick = {
                    handle(PlayerUserAction.PreviousButtonClicked)
                }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = stringResource(R.string.previous),
                        modifier = Modifier.size(48.dp),
                    )
                }
                IconButton(onClick = {
                    handle(PlayerUserAction.PlayToggled)
                }) {
                    Icon(
                        imageVector = state.playbackIcon.icon,
                        contentDescription = stringResource(state.playbackIcon.contentDescription),
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(onClick = {
                    handle(PlayerUserAction.NextButtonClicked)
                }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = stringResource(R.string.previous),
                        modifier = Modifier.size(48.dp),
                    )
                }
            }

            ProgressSlider(
                progress = state.trackProgressState.progress.percent,
                onProgressBarValueChange = { position ->
                    handle(
                        PlayerUserAction.ProgressBarClicked(
                            position = position,
                            trackLength = state.trackProgressState.trackLength
                        )
                    )
                },
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(
                        key = PROGRESS_BAR_SHARED_TRANSITION_KEY
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = state.trackProgressState.currentPlaybackTime.toDisplayableString())
                Text(text = state.trackProgressState.trackLength.toDisplayableString())
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressSlider(
    progress: Float,
    onProgressBarValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
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
    var manualSliderPosition by remember {
        mutableFloatStateOf(progress)
    }
    val sliderPosition by remember(progress, interactions) {
        derivedStateOf {
            if (interactions.isEmpty()) {
                progress
            } else {
                manualSliderPosition
            }
        }
    }

    Slider(
        value = sliderPosition,
        onValueChange = {
            manualSliderPosition = it
        },
        valueRange = 0f..1f,
        onValueChangeFinished = {
            onProgressBarValueChange((manualSliderPosition * Percentage.MAX).toInt())
        },
        modifier = modifier,
        colors = colors,
        thumb = {
            val size by animateIntAsState(
                if (interactions.isNotEmpty()) {
                    PROGRESS_BAR_THUMB_SIZE_LARGE
                } else {
                    PROGRESS_BAR_THUMB_SIZE_SMALL
                },
                label = "size"
            )
            val offset = (-4 * size / 8 + 8).coerceAtLeast(0).dp

            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = null,
                modifier = Modifier
                    .size(size.dp)
                    .offset { IntOffset(x = 0, y = offset.roundToPx()) },
                tint = colors.thumbColor
            )
        },
        track = {
            SliderDefaults.Track(
                sliderState = it,
                modifier = Modifier.height(4.dp),
                drawStopIndicator = null,
                thumbTrackGapSize = 0.000000000.dp
            )
        },
        interactionSource = interactionSource,

    )
}

private const val PROGRESS_BAR_THUMB_SIZE_LARGE = 16
private const val PROGRESS_BAR_THUMB_SIZE_SMALL = 8

@PreviewComponents
@Composable
private fun ProgressbarPreview() {
    ThemedPreview {
        Column {
            ProgressSlider(progress = 0f, onProgressBarValueChange = {})
            ProgressSlider(progress = 0.5f, onProgressBarValueChange = {})
            ProgressSlider(progress = 1f, onProgressBarValueChange = {})
        }
    }
}

@PreviewComponents
@Composable
private fun PlayerCardPreview(
    @PreviewParameter(
        PlayerStatePreviewParameterProvider::class,
        limit = 1
    ) state: PlayerState.Playing
) {
    ThemedPreview {
        AnimatedPlayerCard(
            state = state,
            handle = {}
        )
    }
}

private const val CONTAINER_SHARED_TRANSITION_KEY = "container"
private const val IMAGE_SHARED_TRANSITION_KEY = "image"
private const val TITLE_SHARED_TRANSITION_KEY = "title"
private const val ARTIST_SHARED_TRANSITION_KEY = "artist"
private const val BUTTONS_SHARED_TRANSITION_KEY = "buttons"
private const val PROGRESS_BAR_SHARED_TRANSITION_KEY = "progresBar"

private val ColorScheme.playerContainerColor
    get() = this.surfaceContainerLow
