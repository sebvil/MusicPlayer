package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun CreatePlaylistDialog(onDismiss: () -> Unit, onConfirm: (playlistName: String) -> Unit) {
    var playListName by rememberSaveable {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(playListName) }) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Playlist name")
        },
        text = {
            TextField(
                value = playListName,
                onValueChange = { newValue -> playListName = newValue },
            )
        }
    )
}