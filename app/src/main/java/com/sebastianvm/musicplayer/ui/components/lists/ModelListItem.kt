package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jayasuryat.dowel.annotation.Dowel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListItem


sealed class ModelListItemState(
    open val id: Long,
    open val headlineText: String,
    open val supportingText: String? = null,
    open val mediaArtImageState: MediaArtImageState? = null
) {
    @Dowel(count = 20)
    data class Basic(
        override val id: Long,
        override val headlineText: String,
        override val supportingText: String? = null,
        override val mediaArtImageState: MediaArtImageState? = null
    ) : ModelListItemState(id, headlineText, supportingText, mediaArtImageState)

    @Dowel(count = 20)
    data class WithPosition(
        val position: Long,
        override val id: Long,
        override val headlineText: String,
        override val supportingText: String? = null,
        override val mediaArtImageState: MediaArtImageState? = null
    ) : ModelListItemState(id, headlineText, supportingText, mediaArtImageState)
}


data class ModelListItemStateWithPosition(
    val position: Long,
    val modelListItemState: ModelListItemState
) : DraggableListItem() {
    override val id: Long
        get() = position

    override fun areContentsTheSame(otherItem: DraggableListItem): Boolean {
        return otherItem is ModelListItemStateWithPosition && otherItem == this
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelListItem(
    state: ModelListItemState,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent
) {
    val textColor = contentColorFor(backgroundColor = backgroundColor)
    with(state) {
        ListItem(
            headlineText = {
                Text(
                    text = headlineText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            modifier = modifier,
            supportingText = supportingText?.let {
                {
                    Text(
                        text = it,
                        modifier = Modifier.alpha(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            },
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            colors = ListItemDefaults.colors(
                containerColor = backgroundColor,
                headlineColor = textColor,
                supportingColor = textColor,
                trailingIconColor = textColor,
                leadingIconColor = textColor
            )
        )
    }
}

@Composable
fun ModelListItem(
    state: ModelListItemState,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent
) {
    ModelListItem(
        state = state,
        modifier = modifier,
        trailingContent = trailingContent,
        backgroundColor = backgroundColor,
        leadingContent = {
            state.mediaArtImageState?.let {
                MediaArtImage(
                    uri = it.imageUri,
                    contentDescription = stringResource(
                        id = it.contentDescription,
                        *it.args.toTypedArray()
                    ),
                    backupResource = it.backupResource,
                    backupContentDescription = it.backupContentDescription,
                    modifier = Modifier.size(56.dp)
                )
            }
        })
}

fun Album.toModelListItemState(): ModelListItemState {
    val supportingText = if (year != 0L) "$year $artists" else artists
    return ModelListItemState.Basic(
        id = id,
        headlineText = albumName,
        supportingText = supportingText,
        mediaArtImageState = MediaArtImageState(
            imageUri = imageUri,
            contentDescription = R.string.album_art_for_album,
            backupResource = R.drawable.ic_album,
            backupContentDescription = R.string.placeholder_album_art,
            args = listOf(albumName)
        )
    )
}

fun Artist.toModelListItemState(): ModelListItemState {
    return ModelListItemState.Basic(
        id = id,
        headlineText = artistName,
    )
}

fun Genre.toModelListItemState(): ModelListItemState {
    return ModelListItemState.Basic(
        id = id,
        headlineText = genreName,
    )
}

fun Playlist.toModelListItemState(): ModelListItemState {
    return ModelListItemState.Basic(
        id = id,
        headlineText = playlistName,
    )
}

fun Track.toModelListItemState(): ModelListItemState {
    return ModelListItemState.Basic(id = id, headlineText = trackName, supportingText = artists)
}

fun BasicTrack.toModelListItemState(): ModelListItemState {
    return ModelListItemState.Basic(id = id, headlineText = trackName, supportingText = artists)
}

fun TrackWithPlaylistPositionView.toModelListItemState(): ModelListItemState {
    return ModelListItemState.WithPosition(
        position = position,
        id = id,
        headlineText = trackName,
        supportingText = artists
    )
}

fun TrackWithQueueId.toModelListItemStateWithPosition(): ModelListItemStateWithPosition {
    return ModelListItemStateWithPosition(
        position = this.uniqueQueueItemId,
        modelListItemState = ModelListItemState.Basic(
            id = id,
            headlineText = trackName,
            supportingText = artists
        )
    )
}
