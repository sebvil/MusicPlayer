package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

object TrackRow {
    data class State(val id: Long, val trackName: String, val artists: String?)
}

@Composable
fun TrackRow(
    state: TrackRow.State,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
) {
    ListItem(
        headlineContent = { Text(text = state.trackName) },
        supportingContent = state.artists?.let { artists -> { Text(text = artists) } },
        modifier = modifier,
        trailingContent = trailingContent,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    )
}
