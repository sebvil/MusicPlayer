package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.QueuedTrack
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState

enum class TrailingButtonType {
    More,
    Plus,
    Check
}

data class ModelListItemState(
    val id: Long,
    val headlineContent: String,
    val supportingContent: String? = null,
    val mediaArtImageState: MediaArtImageState? = null,
    val trailingButtonType: TrailingButtonType?,
)

@Composable
fun ModelListItem(
    state: ModelListItemState,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
) {
    with(state) {
        ListItem(
            headlineContent = {
                Text(
                    text = headlineContent,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            modifier = modifier,
            supportingContent =
                supportingContent?.let {
                    {
                        Text(
                            text = it,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
            leadingContent =
                state.mediaArtImageState?.let {
                    { MediaArtImage(mediaArtImageState = it, modifier = Modifier.size(56.dp)) }
                },
            trailingContent = trailingContent,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
        )
    }
}

fun Album.toModelListItemState(): ModelListItemState {
    val supportingContent = if (year != 0L) "$year $artists" else artists
    return ModelListItemState(
        id = id,
        headlineContent = albumName,
        supportingContent = supportingContent,
        mediaArtImageState =
            MediaArtImageState(
                imageUri = imageUri,
                backupImage = com.sebastianvm.musicplayer.designsystem.icons.Icons.Album,
            ),
        trailingButtonType = TrailingButtonType.More,
    )
}

fun Artist.toModelListItemState(trailingButtonType: TrailingButtonType?): ModelListItemState {
    return ModelListItemState(
        id = id,
        headlineContent = artistName,
        trailingButtonType = trailingButtonType,
    )
}

fun Genre.toModelListItemState(): ModelListItemState {
    return ModelListItemState(
        id = id,
        headlineContent = genreName,
        trailingButtonType = TrailingButtonType.More,
    )
}

fun Playlist.toModelListItemState(): ModelListItemState {
    return ModelListItemState(
        id = id,
        headlineContent = playlistName,
        trailingButtonType = TrailingButtonType.More,
    )
}

fun Track.toModelListItemState(): ModelListItemState {
    return ModelListItemState(
        id = id,
        headlineContent = trackName,
        supportingContent = artists,
        trailingButtonType = TrailingButtonType.More,
    )
}

fun BasicTrack.toModelListItemState(trailingButtonType: TrailingButtonType): ModelListItemState {
    return ModelListItemState(
        id = id,
        headlineContent = trackName,
        supportingContent = artists,
        trailingButtonType = trailingButtonType,
    )
}

fun QueuedTrack.toModelListItemState(): ModelListItemState {
    return ModelListItemState(
        id = id,
        headlineContent = trackName,
        supportingContent = artists,
        trailingButtonType = TrailingButtonType.More,
    )
}
