package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import com.sebastianvm.musicplayer.database.entities.TrackWithQueuePosition
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListItem

enum class TrailingButtonType {
    More, Plus, Check
}

sealed class ModelListItemState(
    open val id: Long,
    open val headlineContent: String,
    open val supportingContent: String? = null,
    open val mediaArtImageState: MediaArtImageState? = null,
    open val trailingButtonType: TrailingButtonType?
) {

    data class Basic(
        override val id: Long,
        override val headlineContent: String,
        override val supportingContent: String? = null,
        override val mediaArtImageState: MediaArtImageState? = null,
        override val trailingButtonType: TrailingButtonType?
    ) : ModelListItemState(
        id,
        headlineContent,
        supportingContent,
        mediaArtImageState,
        trailingButtonType
    )

    data class WithPosition(
        val position: Long,
        override val id: Long,
        override val headlineContent: String,
        override val supportingContent: String? = null,
        override val mediaArtImageState: MediaArtImageState? = null,
        override val trailingButtonType: TrailingButtonType?
    ) : ModelListItemState(
        id,
        headlineContent,
        supportingContent,
        mediaArtImageState,
        trailingButtonType
    )
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

@Composable
fun ModelListItem(
    state: ModelListItemState,
    modifier: Modifier = Modifier,
    onMoreClicked: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent
) {
    val textColor = contentColorFor(backgroundColor = backgroundColor)
    with(state) {
        ListItem(
            headlineContent = {
                Text(
                    text = headlineContent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = modifier,
            supportingContent = supportingContent?.let {
                {
                    Text(
                        text = it,
                        modifier = Modifier.alpha(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            leadingContent = state.mediaArtImageState?.let {
                {
                    MediaArtImage(mediaArtImageState = it, modifier = Modifier.size(56.dp))
                }
            },
            trailingContent = when (trailingButtonType) {
                TrailingButtonType.More -> {
                    {
                        IconButton(onClick = onMoreClicked ?: {}) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(
                                    id = R.string.more
                                )
                            )
                        }
                    }
                }

                TrailingButtonType.Plus -> {
                    {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }
                }

                TrailingButtonType.Check -> {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(
                                id = R.string.search
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                null -> null
            },
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

fun Album.toModelListItemState(): ModelListItemState {
    val supportingContent = if (year != 0L) "$year $artists" else artists
    return ModelListItemState.Basic(
        id = id,
        headlineContent = albumName,
        supportingContent = supportingContent,
        mediaArtImageState = MediaArtImageState(
            imageUri = imageUri,
            backupImage = com.sebastianvm.musicplayer.designsystem.icons.Icons.Album
        ),
        trailingButtonType = TrailingButtonType.More
    )
}

fun Artist.toModelListItemState(trailingButtonType: TrailingButtonType?): ModelListItemState {
    return ModelListItemState.Basic(
        id = id,
        headlineContent = artistName,
        trailingButtonType = trailingButtonType

    )
}

fun Genre.toModelListItemState(): ModelListItemState {
    return ModelListItemState.Basic(
        id = id,
        headlineContent = genreName,
        trailingButtonType = TrailingButtonType.More
    )
}

fun Playlist.toModelListItemState(): ModelListItemState {
    return ModelListItemState.Basic(
        id = id,
        headlineContent = playlistName,
        trailingButtonType = TrailingButtonType.More
    )
}

fun Track.toModelListItemState(): ModelListItemState {
    return ModelListItemState.Basic(
        id = id,
        headlineContent = trackName,
        supportingContent = artists,
        trailingButtonType = TrailingButtonType.More
    )
}

fun BasicTrack.toModelListItemState(trailingButtonType: TrailingButtonType): ModelListItemState {
    return ModelListItemState.Basic(
        id = id,
        headlineContent = trackName,
        supportingContent = artists,
        trailingButtonType = trailingButtonType
    )
}

fun TrackWithPlaylistPositionView.toModelListItemState(): ModelListItemState {
    return ModelListItemState.WithPosition(
        position = position,
        id = id,
        headlineContent = trackName,
        supportingContent = artists,
        trailingButtonType = TrailingButtonType.More
    )
}

fun TrackWithQueuePosition.toModelListItemStateWithPosition(): ModelListItemStateWithPosition {
    return ModelListItemStateWithPosition(
        position = this.uniqueQueueItemId,
        modelListItemState = ModelListItemState.Basic(
            id = id,
            headlineContent = trackName,
            supportingContent = artists,
            trailingButtonType = TrailingButtonType.More
        )
    )
}
