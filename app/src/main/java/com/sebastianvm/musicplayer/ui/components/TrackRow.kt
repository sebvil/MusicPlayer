package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.ui.components.lists.DoubleLineListItem
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

data class TrackRowState(
    val trackId: String,
    val trackName: String,
    val artists: String,
    val albumName: String,
    val trackNumber: Long? = null,
) : ListItem {
    override val id = trackId
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrackRowPreview(@PreviewParameter(TrackRowStatePreviewParameterProvider::class) state: TrackRowState) {
    ThemedPreview {
        TrackRow(state = state) {}
    }
}

@Composable
fun TrackRow(state: TrackRowState, modifier: Modifier = Modifier, color: Color = LocalContentColor.current, onOverflowMenuIconClicked: () -> Unit) {
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
                modifier = Modifier.alpha(0.8f).paddingFromBaseline(top = AppDimensions.spacing.mediumLarge),
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


class TrackRowStatePreviewParameterProvider : PreviewParameterProvider<TrackRowState> {
    override val values = sequenceOf(
        TrackRowState("0", "La Promesa", "Melendi", "Un alumno mas"),
    )
}

fun FullTrackInfo.toTrackRowState(includeTrackNumber: Boolean): TrackRowState {
    return TrackRowState(
        trackId = track.trackId,
        trackName = track.trackName,
        artists = artists.joinToString(", ") { it.artistName },
        albumName = album.albumName,
        trackNumber = if (includeTrackNumber) track.trackNumber else null
    )
}
