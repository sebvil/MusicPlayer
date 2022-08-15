package com.sebastianvm.musicplayer.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.ui.components.lists.ListItem
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListItem
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

data class TrackRowState(
    val id: Long,
    val trackId: Long,
    val trackName: String,
    val artists: String,
    val trackNumber: Long? = null,
)

@ComponentPreview
@Composable
fun TrackRowPreview(@PreviewParameter(TrackRowStatePreviewParameterProvider::class) state: TrackRowState) {
    ThemedPreview {
        TrackRow(
            state = state,
            trailingContent = {
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_overflow),
                        contentDescription = stringResource(R.string.more),
                    )
                }
            })
        TrackRow(
            state = state,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            trailingContent = {
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_overflow),
                        contentDescription = stringResource(R.string.more),
                    )
                }
            })


    }
}

@Composable
fun TrackRow(
    state: TrackRowState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        headlineText = state.trackName,
        modifier = modifier,
        supportingText = state.artists,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        backgroundColor = backgroundColor
    )
}

class TrackRowStatePreviewParameterProvider : PreviewParameterProvider<TrackRowState> {
    override val values = sequenceOf(
        TrackRowState(id = 0, trackId = 0, trackName = "La Promesa", artists = "Melendi"),
    )
}

fun Track.toTrackRowState(includeTrackNumber: Boolean): TrackRowState {
    return TrackRowState(
        id = id,
        trackId = id,
        trackName = trackName,
        artists = artists,
        trackNumber = if (includeTrackNumber) trackNumber else null
    )
}

data class DraggableTrackRowState(val uniqueId: String, val trackRowState: TrackRowState) :
    DraggableListItem() {

    override val id: String = uniqueId
    override fun areContentsTheSame(otherItem: DraggableListItem): Boolean {
        return equals(other = otherItem)
    }
}

fun TrackWithQueueId.toDraggableTrackRowState(includeTrackNumber: Boolean): DraggableTrackRowState {
    return DraggableTrackRowState(uniqueQueueItemId, toTrack().toTrackRowState(includeTrackNumber))
}
