package com.sebastianvm.musicplayer.features.playlist.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.components.EmptyScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments

data class PlaylistListUiComponent(val navController: NavController) :
    BaseUiComponent<
        NoArguments,
        PlaylistListState,
        PlaylistListUserAction,
        PlaylistListStateHolder,
    >() {
    override val arguments: NoArguments = NoArguments

    @Composable
    override fun Content(
        state: PlaylistListState,
        handle: Handler<PlaylistListUserAction>,
        modifier: Modifier,
    ) {
        PlaylistList(state = state, handle = handle, modifier = modifier)
    }

    override fun createStateHolder(dependencies: AppDependencies): PlaylistListStateHolder {
        return getPlaylistListStateHolder(
            dependencies = dependencies,
            navController = navController,
        )
    }
}

@Composable
fun PlaylistCreationErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Ok") } },
        title = { Text(text = "Error creating playlist") },
        text = { Text(text = "A playlist with that name already exists.") },
    )
}

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
                            text = stringResource(R.string.no_playlists_try_creating_one),
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
                            Text(text = stringResource(id = R.string.create_playlist))
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
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = null,
        onItemClicked = { _, item -> handle(PlaylistListUserAction.PlaylistClicked(item.id)) },
        onItemMoreIconClicked = { _, item ->
            handle(PlaylistListUserAction.PlaylistMoreIconClicked(item.id))
        },
    )
}
