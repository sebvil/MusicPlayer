package com.sebastianvm.musicplayer.ui.playlist

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.searchfield.SearchField
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate


@Composable
fun TrackSearchScreen(
    screenViewModel: TrackSearchViewModel,
    navigationDelegate: NavigationDelegate
) {
    val context = LocalContext.current
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is TrackSearchUiEvent.ShowConfirmationToast -> {
                    Toast.makeText(
                        context,
                        ResUtil.getString(
                            context,
                            R.string.track_added_to_playlist,
                            event.trackName
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        },
        navigationDelegate = navigationDelegate
    ) { state, delegate ->
        TrackSearchLayout(
            state = state,
            screenDelegate = delegate
        )
    }
}

@Composable
fun AddTrackConfirmationDialog(
    state: AddTrackConfirmationDialogState,
    screenDelegate: ScreenDelegate<TrackSearchUserAction>
) {
    AlertDialog(
        onDismissRequest = {
            screenDelegate.handle(TrackSearchUserAction.CancelAddTrackToPlaylist)
        },
        title = {
            Text(text = stringResource(R.string.add_to_playlist_question))
        },
        text = {
            Text(text = stringResource(id = R.string.song_already_in_playlist, state.trackName))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    screenDelegate.handle(
                        TrackSearchUserAction.ConfirmAddTrackToPlaylist(
                            state.trackId,
                            state.trackName
                        )
                    )
                }
            ) {
                Text(stringResource(R.string.add_to_playlist))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    screenDelegate.handle(TrackSearchUserAction.CancelAddTrackToPlaylist)
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TrackSearchLayout(
    state: TrackSearchState,
    screenDelegate: ScreenDelegate<TrackSearchUserAction>,
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    state.addTrackConfirmationDialogState?.also {
        AddTrackConfirmationDialog(state = it, screenDelegate = screenDelegate)
    }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .focusRequester(focusRequester)
            .focusable(enabled = true, interactionSource)
            .clickable { focusRequester.requestFocus() }) {
        SearchField(
            onTextChanged = { screenDelegate.handle(TrackSearchUserAction.TextChanged(it)) },
            onUpButtonClicked = { screenDelegate.handle(TrackSearchUserAction.UpButtonClicked) },
            focusRequester = focusRequester
        )

        Row(
            Modifier.padding(horizontal = AppDimensions.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = state.hideTracksInPlaylist,
                onCheckedChange = { screenDelegate.handle(TrackSearchUserAction.HideTracksCheckToggled) })
            Text(
                text = stringResource(R.string.hide_tracks_in_playlist),
                modifier = Modifier.padding(start = AppDimensions.spacing.large)
            )
        }

        LazyColumn {
            items(state.trackSearchResults) { item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier
                        .clickable {
                            screenDelegate.handle(
                                TrackSearchUserAction.TrackClicked(
                                    trackId = item.id,
                                    trackName = item.headlineText
                                )
                            )
                        },
                    trailingContent = {
                        if (item.id in state.playlistTrackIds) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(
                                    id = R.string.search
                                ),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_plus),
                                contentDescription = stringResource(R.string.more),
                            )
                        }
                    }
                )
            }
        }
    }
}


