package com.sebastianvm.musicplayer.features.playlist.list

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.designsystem.components.Text

@Composable
fun CreatePlaylistDialog(onDismiss: () -> Unit, onConfirm: (playlistName: String) -> Unit) {
    var playListName by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(playListName) }) {
                Text(text = stringResource(RString.create))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text(text = stringResource(RString.cancel)) }
        },
        title = { Text(text = "Playlist name") },
        text = {
            TextField(
                value = playListName,
                onValueChange = { newValue -> playListName = newValue },
                modifier = Modifier.focusRequester(focusRequester),
            )
        },
    )
}
