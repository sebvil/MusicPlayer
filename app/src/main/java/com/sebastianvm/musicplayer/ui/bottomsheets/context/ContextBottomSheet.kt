package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun <S : BaseContextMenuState> ContextBottomSheet(
    sheetViewModel: BaseContextMenuViewModel<S> = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = sheetViewModel,
        navigationDelegate = navigationDelegate
    ) { state, screenDelegate ->
        ContextMenuLayout(state = state, screenDelegate = screenDelegate)
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextMenuLayout(
    state: BaseContextMenuState,
    screenDelegate: ScreenDelegate<BaseContextMenuUserAction>
) {
    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                screenDelegate.handle(BaseContextMenuUserAction.DismissPlaybackErrorDialog)
            }
        })

    if (state is PlaylistContextMenuState && state.showDeleteConfirmationDialog) {
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
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimensions.bottomSheet.rowHeight)
                        .padding(start = AppDimensions.bottomSheet.startPadding)
                ) {
                    Text(
                        text = state.menuTitle,
                        modifier = Modifier.paddingFromBaseline(top = 36.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth())
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
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
}
