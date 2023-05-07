package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import com.sebastianvm.musicplayer.ui.components.AnimatedTextOverflow

data class TrackInfoState(val trackName: String, val artists: String)

@Composable
fun TrackInfo(
    state: TrackInfoState,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge
) {
    Column(modifier = modifier) {
        AnimatedTextOverflow(
            text = state.trackName,
            style = style,
        )
        AnimatedTextOverflow(
            text = state.artists,
            modifier = Modifier.alpha(0.6f),
            style = style,
        )
    }
}