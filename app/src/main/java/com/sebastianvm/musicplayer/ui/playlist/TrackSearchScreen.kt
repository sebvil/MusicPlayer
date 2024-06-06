package com.sebastianvm.musicplayer.ui.playlist

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.searchfield.SearchField
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

@Composable
fun AddTrackConfirmationDialog(
    state: AddTrackConfirmationDialogState,
    handle: Handler<TrackSearchUserAction>,
    updateTrackName: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { handle(TrackSearchUserAction.CancelAddTrackToPlaylist) },
        title = { Text(text = stringResource(R.string.add_to_playlist_question)) },
        text = {
            Text(text = stringResource(id = R.string.song_already_in_playlist, state.trackName))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    handle(
                        TrackSearchUserAction.ConfirmAddTrackToPlaylist(
                            state.trackId,
                            state.trackName,
                        )
                    )
                    updateTrackName(state.trackName)
                }
            ) {
                Text(stringResource(R.string.add_to_playlist))
            }
        },
        dismissButton = {
            TextButton(onClick = { handle(TrackSearchUserAction.CancelAddTrackToPlaylist) }) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun TrackSearchLayout(
    state: TrackSearchState,
    handle: Handler<TrackSearchUserAction>,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    var trackName by remember { mutableStateOf("") }
    state.addTrackConfirmationDialogState?.also {
        AddTrackConfirmationDialog(
            state = it,
            handle = handle,
            updateTrackName = { newName -> trackName = newName },
        )
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = state.showToast) {
        if (state.showToast) {
            Toast.makeText(
                    context,
                    context.getString(R.string.track_added_to_playlist, trackName),
                    Toast.LENGTH_SHORT,
                )
                .show()
            handle(TrackSearchUserAction.ToastShown)
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .focusRequester(focusRequester)
                .focusable(enabled = true, interactionSource)
                .clickable { focusRequester.requestFocus() }
    ) {
        SearchField(
            onTextChanged = { handle(TrackSearchUserAction.TextChanged(it)) },
            onUpButtonClicked = {
                //                navigator.navigateUp()
            },
            focusRequester = focusRequester,
        )

        ListItem(
            leadingContent = {
                Switch(
                    checked = state.hideTracksInPlaylist,
                    onCheckedChange = { handle(TrackSearchUserAction.HideTracksCheckToggled) },
                )
            },
            headlineContent = { Text(text = stringResource(R.string.hide_tracks_in_playlist)) },
        )

        LazyColumn(contentPadding = LocalPaddingValues.current) {
            items(state.trackSearchResults) { item ->
                ModelListItem(
                    state = item,
                    modifier =
                        Modifier.clickable {
                            handle(
                                TrackSearchUserAction.TrackClicked(
                                    trackId = item.id,
                                    trackName = item.headlineContent,
                                )
                            )
                            trackName = item.headlineContent
                        },
                )
            }
        }
    }
}
