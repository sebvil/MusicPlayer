package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.sebastianvm.model.BasicTrack
import com.sebastianvm.model.Track

object TrackRow {
    data class State(val id: Long, val trackName: String, val artists: String?) {
        companion object {
            fun fromTrack(track: Track): State {
                return State(
                    id = track.id,
                    trackName = track.name,
                    artists = track.artists.joinToString { it.name },
                )
            }

            fun fromTrack(track: BasicTrack): State {
                return State(id = track.id, trackName = track.name, artists = track.artists)
            }
        }
    }
}

@Composable
fun TrackRow(
    state: TrackRow.State,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
) {
    ListItem(
        headlineContent = { Text(text = state.trackName) },
        supportingContent = state.artists?.let { artists -> { Text(text = artists) } },
        modifier = modifier,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    )
}
