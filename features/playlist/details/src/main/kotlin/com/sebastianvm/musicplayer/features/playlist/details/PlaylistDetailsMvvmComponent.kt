package com.sebastianvm.musicplayer.features.playlist.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
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
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.kspannotations.MvvmComponent

@OptIn(ExperimentalMaterial3Api::class)
@MvvmComponent(vmClass = PlaylistDetailsViewModel::class)
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
                    TopAppBar(
                        title = { Text(text = state.playlistName) },
                        navigationIcon = {
                            IconButton(
                                onClick = { handle(PlaylistDetailsUserAction.BackClicked) }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = stringResource(id = RString.back),
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    handle(PlaylistDetailsUserAction.EditPlaylistNameClicked)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(id = RString.back),
                                )
                            }
                        },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetails(
    state: PlaylistDetailsState.Data,
    handle: Handler<PlaylistDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    state.playlistNameDialog?.Content(Modifier)
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
            TopAppBar(
                title = { Text(text = state.playlistName) },
                navigationIcon = {
                    IconButton(onClick = { handle(PlaylistDetailsUserAction.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = RString.back),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { handle(PlaylistDetailsUserAction.EditPlaylistNameClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = RString.back),
                        )
                    }
                },
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
