package com.sebastianvm.musicplayer.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult

@Composable
fun PlaybackStatusIndicator(
    playbackResult: PlaybackResult?,
    delegate: PlaybackStatusIndicatorDelegate
) {
    when (playbackResult) {
        is PlaybackResult.Loading -> {
            Dialog(onDismissRequest = { delegate.onDismissRequest() }) {
                CircularProgressIndicator()
            }
        }
        is PlaybackResult.Error -> {
            AlertDialog(
                onDismissRequest = { delegate.onDismissRequest() },
                confirmButton = {
                    TextButton(onClick = { delegate.onDismissRequest() }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                },
                title = { Text(text = stringResource(id = R.string.playback_error)) },
                text = { Text(text = stringResource(id = playbackResult.errorMessage)) }
            )
        }
        else -> Unit
    }
}

interface PlaybackStatusIndicatorDelegate {
    fun onDismissRequest() = Unit
}
