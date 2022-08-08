package com.sebastianvm.musicplayer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.ui.components.lists.DoubleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListItem
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

data class TrackRowState(
    val trackId: Long,
    val trackName: String,
    val artists: String,
    val albumName: String,
    val trackNumber: Long? = null,
)

@ComponentPreview
@Composable
fun TrackRowPreview(@PreviewParameter(TrackRowStatePreviewParameterProvider::class) state: TrackRowState) {
    ThemedPreview {
        Column {
            TrackRow(state = state) {}
            TrackRow(state = state, trailingContent = {
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
}

@Composable
fun TrackRow(
    state: TrackRowState,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    onOverflowMenuIconClicked: () -> Unit,
) {
    DoubleLineListItem(
        modifier = modifier,
        afterListContent = {
            IconButton(
                onClick = onOverflowMenuIconClicked,
                modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_overflow),
                    contentDescription = stringResource(R.string.more),
                    tint = color,
                )
            }
        },
        secondaryText = {
            Text(
                text = state.artists,
                modifier = Modifier
                    .alpha(0.8f)
                    .paddingFromBaseline(top = AppDimensions.spacing.mediumLarge),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                color = color,
                overflow = TextOverflow.Ellipsis
            )
        }) {
        Text(
            text = state.trackName,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            color = color,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.paddingFromBaseline(top = AppDimensions.spacing.xLarge)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackRow(
    state: TrackRowState,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        headlineText = {
            Text(
                text = state.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
        supportingText = {
            Text(
                text = state.artists,
                modifier = Modifier.alpha(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = trailingContent
    )
}

class TrackRowStatePreviewParameterProvider : PreviewParameterProvider<TrackRowState> {
    override val values = sequenceOf(
        TrackRowState(0, "La Promesa", "Melendi", "Un alumno mas"),
    )
}

fun Track.toTrackRowState(includeTrackNumber: Boolean): TrackRowState {
    return TrackRowState(
        trackId = id,
        trackName = trackName,
        artists = artists,
        albumName = albumName,
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
