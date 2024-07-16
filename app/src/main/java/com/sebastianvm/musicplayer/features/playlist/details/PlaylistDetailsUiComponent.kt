package com.sebastianvm.musicplayer.features.playlist.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.core.designsystems.components.EmptyScreen
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.track.list.TopBar
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

data class PlaylistDetailsUiComponent(
    override val arguments: PlaylistDetailsArguments,
    val navController: NavController,
) :
    BaseUiComponent<
        PlaylistDetailsArguments,
        PlaylistDetailsState,
        PlaylistDetailsUserAction,
        PlaylistDetailsStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): PlaylistDetailsStateHolder {
        return PlaylistDetailsStateHolder(
            args = arguments,
            navController = navController,
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
            playbackManager = dependencies.playbackManager,
            playlistRepository = dependencies.repositoryProvider.playlistRepository,
        )
    }

    @Composable
    override fun Content(
        state: PlaylistDetailsState,
        handle: Handler<PlaylistDetailsUserAction>,
        modifier: Modifier,
    ) {
        PlaylistDetails(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun PlaylistDetails(
    state: PlaylistDetailsState,
    handle: Handler<PlaylistDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is PlaylistDetailsState.Loading -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopBar(
                        title = state.playlistName,
                        onBackButtonClick = { handle(PlaylistDetailsUserAction.BackClicked) },
                    )
                },
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(it)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
        is PlaylistDetailsState.Data -> {
            PlaylistDetails(state = state, handle = handle, modifier = modifier)
        }
    }
}

@Composable
fun PlaylistDetails(
    state: PlaylistDetailsState.Data,
    handle: Handler<PlaylistDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (state.tracks.isEmpty()) return@Scaffold
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = RString.add_tracks)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onClick = { handle(PlaylistDetailsUserAction.AddTracksButtonClicked) },
                modifier = Modifier.padding(LocalPaddingValues.current),
            )
        },
        topBar = {
            TopBar(
                title = state.playlistName,
                onBackButtonClick = { handle(PlaylistDetailsUserAction.BackClicked) },
            )
        },
    ) { paddingValues ->
        PlaylistDetailsLayout(
            state = state,
            handle = handle,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun PlaylistDetailsLayout(
    state: PlaylistDetailsState.Data,
    handle: Handler<PlaylistDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    if (state.tracks.isEmpty()) {
        EmptyScreen(
            message = {
                Text(
                    text = stringResource(id = RString.playlist_is_empty),
                    textAlign = TextAlign.Center,
                )
            },
            button = {
                Button(onClick = { handle(PlaylistDetailsUserAction.AddTracksButtonClicked) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text(text = stringResource(id = RString.add_tracks))
                }
            },
            modifier = modifier.fillMaxSize(),
        )
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = LocalPaddingValues.current,
        ) {
            item {
                SortButton(
                    state = state.sortButtonState,
                    onClick = { handle(PlaylistDetailsUserAction.SortButtonClicked) },
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            itemsIndexed(state.tracks, key = { index, item -> index to item.id }) { index, item ->
                TrackRow(
                    state = item,
                    modifier =
                        Modifier.animateItem().clickable {
                            handle(PlaylistDetailsUserAction.TrackClicked(trackIndex = index))
                        },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                handle(
                                    PlaylistDetailsUserAction.TrackMoreIconClicked(
                                        trackId = item.id,
                                        trackPositionInList = index,
                                    ))
                            }) {
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
