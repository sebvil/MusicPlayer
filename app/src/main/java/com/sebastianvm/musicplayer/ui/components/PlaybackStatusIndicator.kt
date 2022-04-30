package com.sebastianvm.musicplayer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult

interface PlaybackStatusIndicatorDelegate {
    fun onDismissRequest() = Unit
}

@Composable
fun PlaybackStatusIndicator(
    playbackResult: PlaybackResult?,
    delegate: PlaybackStatusIndicatorDelegate
) {
    when (playbackResult) {
        is PlaybackResult.Loading -> {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(), contentAlignment = Alignment.Center) {
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