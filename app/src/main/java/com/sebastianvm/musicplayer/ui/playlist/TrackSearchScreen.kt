package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSearchLayout(
    state: TrackSearchState,
    screenDelegate: ScreenDelegate<TrackSearchUserAction>,
) {
    val input = rememberSaveable {
        mutableStateOf("")
    }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    state.addTrackConfirmationDialogState?.also {
        AlertDialog(
            onDismissRequest = {
                screenDelegate.handle(TrackSearchUserAction.CancelAddTrackToPlaylist)
            },
            title = {
                Text(text = stringResource(R.string.add_to_playlist_question))
            },
            text = {
                Text(text = stringResource(id = R.string.song_already_in_playlist, it.trackName))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        screenDelegate.handle(
                            TrackSearchUserAction.ConfirmAddTrackToPlaylist(
                                it.trackId,
                                it.trackName
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .focusRequester(focusRequester)
            .focusable(enabled = true, interactionSource)
            .clickable { focusRequester.requestFocus() }) {
        TextField(
            value = input.value,
            onValueChange = {
                input.value = it
                screenDelegate.handle(TrackSearchUserAction.TextChanged(it))
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    style = LocalTextStyle.current,
                    color = LocalContentColor.current
                )
            },
            leadingIcon = input.value.takeIf { it.isEmpty() }?.let {
                {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(
                            id = R.string.search
                        )
                    )
                }
            },
            trailingIcon = input.value.takeUnless { it.isEmpty() }?.let {
                {
                    IconButton(onClick = {
                        input.value = ""
                        screenDelegate.handle(TrackSearchUserAction.TextChanged(it))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }
                }
            },
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth()
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


