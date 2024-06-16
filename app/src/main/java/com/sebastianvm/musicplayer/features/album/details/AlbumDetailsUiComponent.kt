package com.sebastianvm.musicplayer.features.album.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.track.list.TopBar
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.util.resources.RString
import kotlin.math.roundToInt

data class AlbumDetailsUiComponent(
    override val arguments: AlbumDetailsArguments,
    val navController: NavController,
) :
    BaseUiComponent<
        AlbumDetailsArguments,
        AlbumDetailsState,
        AlbumDetailsUserAction,
        AlbumDetailsStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): AlbumDetailsStateHolder {
        return AlbumDetailsStateHolder(
            args = arguments,
            navController = navController,
            albumRepository = dependencies.repositoryProvider.albumRepository,
            playbackManager = dependencies.repositoryProvider.playbackManager,
        )
    }

    @Composable
    override fun Content(
        state: AlbumDetailsState,
        handle: Handler<AlbumDetailsUserAction>,
        modifier: Modifier,
    ) {
        AlbumDetails(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun AlbumDetails(
    state: AlbumDetailsState,
    handle: Handler<AlbumDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val minSize = with(density) { 100.dp.toPx() }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxSize = with(density) { (screenWidth - 200.dp).coerceAtMost(500.dp).toPx() }
    var size by remember { mutableFloatStateOf(maxSize) }
    var offset by remember { mutableFloatStateOf(0f) }

    var fullHeaderHeight by remember { mutableFloatStateOf(0f) }
    var topBarHeight by remember { mutableFloatStateOf(0f) }
    val topPadding =
        with(density) { WindowInsets.systemBars.asPaddingValues().calculateTopPadding().toPx() }

    val topBarAlpha by remember {
        derivedStateOf {
            if (-offset < minSize - topBarHeight + topPadding) {
                0f
            } else {
                val visibleHeaderHeight = fullHeaderHeight - topBarHeight + offset
                1f - visibleHeaderHeight / (fullHeaderHeight - minSize - topPadding)
            }
        }
    }

    val imageAlpha by remember {
        derivedStateOf {
            if (size > minSize) {
                1f
            } else {
                val target = minSize - topBarHeight + topPadding
                (target + offset).coerceAtLeast(0f) / target
            }
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0f) return Offset.Zero
                if (!listState.canScrollForward) return Offset.Zero
                val oldSize = size
                size = (available.y + size).coerceAtLeast(minSize)
                return if (oldSize != size) {
                    available.copy(x = 0f)
                } else {
                    val oldOffset = offset
                    offset = (available.y + offset).coerceIn(topBarHeight - fullHeaderHeight, 0f)
                    if (oldOffset != offset) {
                        available.copy(x = 0f)
                    } else {
                        Offset.Zero
                    }
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (!listState.canScrollForward) return Offset.Zero
                return if (offset == 0f) {
                    val oldSize = size
                    size = (size + available.y).coerceAtMost(maxSize)
                    if (oldSize != size) {
                        available.copy(x = 0f)
                    } else {
                        Offset.Zero
                    }
                } else {
                    val oldOffset = offset
                    offset = (available.y + offset).coerceAtMost(0f)
                    if (oldOffset != offset) {
                        available.copy(x = 0f)
                    } else {
                        Offset.Zero
                    }
                }
            }
        }
    }

    val animatedSize = animateFloatAsState(targetValue = size, label = "size")
    val animatedOffset = animateFloatAsState(targetValue = offset, label = "size")

    SubcomposeLayout(modifier = modifier.nestedScroll(nestedScrollConnection)) { constraints ->
        val topBarPlaceables =
            subcompose(AlbumDetailsContent.TopBar) {
                    TopBar(
                        title = state.albumName,
                        alpha = topBarAlpha,
                        onBackButtonClick = { handle(AlbumDetailsUserAction.BackClicked) },
                    )
                }
                .fastMap { it.measure(constraints) }

        topBarHeight = topBarPlaceables.fastMaxBy { it.height }?.height?.toFloat() ?: 0f

        val headerPlaceables =
            subcompose(AlbumDetailsContent.Header) {
                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(
                                    top =
                                        WindowInsets.systemBars
                                            .asPaddingValues()
                                            .calculateTopPadding()
                                ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        MediaArtImage(
                            artworkUri = state.imageUri,
                            modifier = Modifier.stateSize(animatedSize).alpha(imageAlpha),
                        )

                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = state.albumName,
                                style =
                                    MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.alpha(1 - topBarAlpha),
                            )

                            state.artists?.let {
                                Text(
                                    text = it,
                                    style =
                                        MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth().alpha(1 - topBarAlpha),
                                )
                            }
                        }
                    }
                }
                .fastMap { it.measure(constraints) }

        fullHeaderHeight = headerPlaceables.fastMaxBy { it.height }?.height?.toFloat() ?: 0f

        val contentPlaceables =
            subcompose(AlbumDetailsContent.Content) {
                    when (state) {
                        is AlbumDetailsState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        is AlbumDetailsState.Data -> {
                            AlbumDetails(
                                state = state,
                                handle = handle,
                                contentPadding =
                                    PaddingValues(
                                        bottom =
                                            (LocalPaddingValues.current.calculateBottomPadding() +
                                                    animatedOffset.value.toDp())
                                                .coerceAtLeast(0.dp),
                                        top = fullHeaderHeight.toDp()
                                    ),
                                listState = listState,
                            )
                        }
                    }
                }
                .fastMap { it.measure(constraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            topBarPlaceables.fastMap { it.place(0, 0) }
            headerPlaceables.fastMap { it.place(0, animatedOffset.value.roundToInt()) }
            contentPlaceables.fastMap { it.place(0, animatedOffset.value.roundToInt()) }
        }
    }
}

@Composable
fun AlbumDetails(
    state: AlbumDetailsState.Data,
    handle: Handler<AlbumDetailsUserAction>,
    contentPadding: PaddingValues,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    if (state.tracks.isEmpty()) {
        StoragePermissionNeededEmptyScreen(
            message = RString.no_tracks_found,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        )
    } else {
        LazyColumn(modifier = modifier, contentPadding = contentPadding, state = listState) {
            itemsIndexed(state.tracks, key = { index, item -> index to item.id }) { index, item ->
                TrackRow(
                    state = item,
                    modifier =
                        Modifier.animateItem().clickable {
                            handle(AlbumDetailsUserAction.TrackClicked(trackIndex = index))
                        },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                handle(
                                    AlbumDetailsUserAction.TrackMoreIconClicked(
                                        trackId = item.id,
                                        trackPositionInList = index,
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(id = RString.more),
                            )
                        }
                    },
                )
            }
        }
    }
}

private fun Modifier.stateSize(size: State<Float>) =
    then(
        Modifier.layout { measurable, constraints ->
            val sizeValue = size.value.roundToInt()
            val placeable =
                measurable.measure(
                    constraints.copy(
                        minWidth = sizeValue,
                        minHeight = sizeValue,
                        maxWidth = sizeValue,
                        maxHeight = sizeValue
                    )
                )
            layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
        }
    )

private enum class AlbumDetailsContent {
    TopBar,
    Header,
    Content
}
