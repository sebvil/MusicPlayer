package com.sebastianvm.musicplayer.features.playlist.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.core.designsystems.components.EmptyScreen
import com.sebastianvm.musicplayer.core.designsystems.components.OverflowIconButton
import com.sebastianvm.musicplayer.core.designsystems.components.PlaylistRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.kspannotations.MvvmComponent

@Composable
fun PlaylistCreationErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Ok") } },
        title = { Text(text = "Error creating playlist") },
        text = { Text(text = "A playlist with that name already exists.") },
    )
}

@MvvmComponent(vmClass = PlaylistListViewModel::class)
@Composable
fun PlaylistList(
    state: PlaylistListState,
    handle: Handler<PlaylistListUserAction>,
    modifier: Modifier = Modifier,
) {

    if (state.isCreatePlaylistDialogOpen) {
        CreatePlaylistDialog(
            onDismiss = { handle(PlaylistListUserAction.DismissPlaylistCreationDialog) },
            onConfirm = { handle(PlaylistListUserAction.CreatePlaylistButtonClicked(it)) },
        )
    }

    if (state.isPlaylistCreationErrorDialogOpen) {
        PlaylistCreationErrorDialog(
            onDismiss = { handle(PlaylistListUserAction.DismissPlaylistCreationErrorDialog) }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (state) {
            is PlaylistListState.Data -> {
                PlaylistListLayout(state = state, handle = handle)
            }
            is PlaylistListState.Empty -> {
                EmptyScreen(
                    message = {
                        Text(
                            text = stringResource(RString.no_playlists_try_creating_one),
                            textAlign = TextAlign.Center,
                        )
                    },
                    button = {
                        Button(
                            onClick = {
                                handle(PlaylistListUserAction.CreateNewPlaylistButtonClicked)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Text(text = stringResource(id = RString.create_playlist))
                        }
                    },
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                )
            }
            is PlaylistListState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun PlaylistListLayout(
    state: PlaylistListState.Data,
    handle: Handler<PlaylistListUserAction>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (state.playlists.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(LocalPaddingValues.current),
                    onClick = { handle(PlaylistListUserAction.CreateNewPlaylistButtonClicked) },
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text(text = stringResource(id = RString.new_playlist))
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = LocalPaddingValues.current,
        ) {
            item {
                SortButton(
                    state = state.sortButtonState,
                    onClick = { handle(PlaylistListUserAction.SortByClicked) },
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
            items(state.playlists, key = { item -> item.id }) { item ->
                PlaylistRow(
                    state = item,
                    modifier =
                        Modifier.clickable {
                            handle(
                                PlaylistListUserAction.PlaylistClicked(
                                    playlistId = item.id,
                                    playlistName = item.playlistName,
                                )
                            )
                        },
                    trailingContent = {
                        OverflowIconButton(
                            onClick = {
                                handle(PlaylistListUserAction.PlaylistMoreIconClicked(item.id))
                            }
                        )
                    },
                )
            }
        }
    }
}
