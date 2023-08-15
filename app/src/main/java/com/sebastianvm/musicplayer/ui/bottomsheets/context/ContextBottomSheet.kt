package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegateImpl
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun ContextBottomSheet(
    navigator: DestinationsNavigator,
    sheetViewModel: BaseContextMenuViewModel,
) {
    Screen(
        screenViewModel = sheetViewModel,
        navigationDelegate = NavigationDelegateImpl(navigator)
    ) { state, screenDelegate ->
        ContextMenuLayout(state = state, screenDelegate = screenDelegate)
    }
}


@RootNavGraph
@Destination(
    navArgsDelegate = TrackContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun TrackContextMenu(
    navigator: DestinationsNavigator,
    viewModel: TrackContextMenuViewModel = hiltViewModel()
) {
    ContextBottomSheet(navigator = navigator, sheetViewModel = viewModel)
}

@RootNavGraph
@Destination(
    navArgsDelegate = ArtistContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun ArtistContextMenu(
    navigator: DestinationsNavigator,
    viewModel: ArtistContextMenuViewModel = hiltViewModel()
) {
    ContextBottomSheet(navigator = navigator, sheetViewModel = viewModel)
}

@RootNavGraph
@Destination(
    navArgsDelegate = AlbumContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun AlbumContextMenu(
    navigator: DestinationsNavigator,
    viewModel: AlbumContextMenuViewModel = hiltViewModel()
) {
    ContextBottomSheet(navigator = navigator, sheetViewModel = viewModel)
}

@RootNavGraph
@Destination(
    navArgsDelegate = GenreContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun GenreContextMenu(
    navigator: DestinationsNavigator,
    viewModel: GenreContextMenuViewModel = hiltViewModel()
) {
    ContextBottomSheet(navigator = navigator, sheetViewModel = viewModel)
}

@RootNavGraph
@Destination(
    navArgsDelegate = PlaylistContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun PlaylistContextMenu(
    navigator: DestinationsNavigator,
    viewModel: PlaylistContextMenuViewModel = hiltViewModel()
) {
    ContextBottomSheet(navigator = navigator, sheetViewModel = viewModel)
}

interface DeletePlaylistConfirmationDialogDelegate {
    fun onDismissDialog() = Unit
    fun onSubmit() = Unit
}

@Composable
fun DeletePlaylistConfirmationDialog(
    playlistName: String,
    delegate: DeletePlaylistConfirmationDialogDelegate
) {

    AlertDialog(
        onDismissRequest = { delegate.onDismissDialog() },
        confirmButton = {
            TextButton(onClick = { delegate.onSubmit() }) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = { delegate.onDismissDialog() }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.delete_this_playlist, playlistName))
        },
        text = {
            Text(text = stringResource(id = R.string.sure_you_want_to_delete, playlistName))
        }
    )
}

@Composable
fun ContextMenuLayout(
    state: ContextMenuState,
    screenDelegate: ScreenDelegate<BaseContextMenuUserAction>
) {
    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                screenDelegate.handle(BaseContextMenuUserAction.DismissPlaybackErrorDialog)
            }
        })

    if (state.showDeleteConfirmationDialog) {
        DeletePlaylistConfirmationDialog(
            playlistName = state.menuTitle,
            delegate = object : DeletePlaylistConfirmationDialogDelegate {
                override fun onDismissDialog() {
                    screenDelegate.handle(BaseContextMenuUserAction.CancelDeleteClicked)
                }

                override fun onSubmit() {
                    screenDelegate.handle(BaseContextMenuUserAction.ConfirmDeleteClicked)
                }
            }
        )
        // Need this to be able to dismiss bottom sheet after deleting playlist
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
    } else {
        with(state) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = state.menuTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.padding(top = 12.dp)
                )

                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                LazyColumn {
                    items(listItems, key = { it.text }) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(id = it.text),
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            modifier = Modifier.clickable {
                                screenDelegate.handle(
                                    BaseContextMenuUserAction.RowClicked(it)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(id = it.icon),
                                    contentDescription = stringResource(id = it.text),
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
