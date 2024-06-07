package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.designsystem.components.ListItem
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState

data object ModelListItem {
    data class State(
        val id: Long,
        val headlineContent: String,
        val supportingContent: String? = null,
        val mediaArtImageState: MediaArtImageState? = null,
    )
}

@Deprecated(
    message = "",
    replaceWith =
        ReplaceWith(
            "ListItem(\n" +
                "            headlineContent = { Text(text = state.headlineContent) },\n" +
                "            modifier = modifier,\n" +
                "            supportingContent =\n" +
                "                state.supportingContent?.let {\n" +
                "                    {\n" +
                "                        Text(\n" +
                "                            text = it,\n" +
                "                        )\n" +
                "                    }\n" +
                "                },\n" +
                "            leadingContent =\n" +
                "                state.mediaArtImageState?.let {\n" +
                "                    { MediaArtImage(mediaArtImageState = it, modifier = Modifier.size(56.dp)) }\n" +
                "                },\n" +
                "            trailingContent = trailingContent,\n" +
                "            tonalElevation = tonalElevation,\n" +
                "            shadowElevation = shadowElevation,\n" +
                "        )"
        )
)
@Composable
fun ModelListItem(
    state: ModelListItem.State,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
) {
    with(state) {
        ListItem(
            headlineContent = { Text(text = headlineContent) },
            modifier = modifier,
            supportingContent =
                supportingContent?.let {
                    {
                        Text(
                            text = it,
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

fun Album.toModelListItemState(): ModelListItem.State {
    val supportingContent = if (year != 0L) "$year $artists" else artists
    return ModelListItem.State(
        id = id,
        headlineContent = albumName,
        supportingContent = supportingContent,
        mediaArtImageState =
            MediaArtImageState(
                imageUri = imageUri,
                backupImage = com.sebastianvm.musicplayer.designsystem.icons.Icons.Album,
            ),
    )
}

fun Artist.toModelListItemState(): ModelListItem.State {
    return ModelListItem.State(
        id = id,
        headlineContent = artistName,
    )
}

fun Genre.toModelListItemState(): ModelListItem.State {
    return ModelListItem.State(
        id = id,
        headlineContent = genreName,
    )
}

fun Playlist.toModelListItemState(): ModelListItem.State {
    return ModelListItem.State(
        id = id,
        headlineContent = playlistName,
    )
}

fun Track.toModelListItemState(): ModelListItem.State {
    return ModelListItem.State(
        id = id,
        headlineContent = trackName,
        supportingContent = artists,
    )
}

fun BasicTrack.toModelListItemState(): ModelListItem.State {
    return ModelListItem.State(
        id = id,
        headlineContent = trackName,
        supportingContent = artists,
    )
}
