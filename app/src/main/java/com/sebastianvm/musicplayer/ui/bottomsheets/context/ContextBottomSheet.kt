package com.sebastianvm.musicplayer.ui.bottomsheets.context

import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleNavEvents
import kotlinx.coroutines.Dispatchers

@Composable
fun <S : BaseContextMenuState> ContextBottomSheet(
    sheetViewModel: BaseContextMenuViewModel<S> = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    val state = sheetViewModel.stateFlow.collectAsState(context = Dispatchers.Main)
    val context = LocalContext.current
    HandleNavEvents(viewModel = sheetViewModel, navigationDelegate = navigationDelegate)
    HandleEvents(viewModel = sheetViewModel) { event ->
        when (event) {
            is BaseContextMenuUiEvent.ShowToast -> {
                Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    ContextMenuLayout(state = state.value, object : ContextMenuDelegate {
        override fun onRowClicked(contextMenuItem: ContextMenuItem) {
            sheetViewModel.onRowClicked(contextMenuItem)
        }

        override fun onDismissDialog() {
            (sheetViewModel as? PlaylistContextMenuViewModel)?.onCancelDeleteClicked()
        }

        override fun onSubmit() {
            (sheetViewModel as? PlaylistContextMenuViewModel)?.onConfirmDeleteClicked()
        }

        override fun onDismissRequest() {
            sheetViewModel.onPlaybackErrorDismissed()
        }
    })
}

/**
 * The Android Studio Preview cannot handle this, but it can be run in device for preview
 */
@ComponentPreview
@Composable
fun ContextMenuScreenPreview(@PreviewParameter(ContextMenuStatePreviewParameterProvider::class) state: BaseContextMenuState) {
    ThemedPreview {
        ContextMenuLayout(state = state, object : ContextMenuDelegate {})
    }
}

interface ContextMenuDelegate : DeletePlaylistConfirmationDialogDelegate,
    PlaybackStatusIndicatorDelegate {
    fun onRowClicked(contextMenuItem: ContextMenuItem) = Unit
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
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { delegate.onDismissDialog() }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Delete playlist $playlistName")
        },
        text = {
            Text(text = "Are you sure you want to delete $playlistName?")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextMenuLayout(
    state: BaseContextMenuState,
    delegate: ContextMenuDelegate
) {
    PlaybackStatusIndicator(playbackResult = state.playbackResult, delegate = delegate)

    if (state is PlaylistContextMenuState && state.showDeleteConfirmationDialog) {
        DeletePlaylistConfirmationDialog(
            playlistName = state.menuTitle,
            delegate = delegate
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
                            headlineText = {
                                Text(
                                    text = stringResource(id = it.text),
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            modifier = Modifier.clickable { delegate.onRowClicked(it) },
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
