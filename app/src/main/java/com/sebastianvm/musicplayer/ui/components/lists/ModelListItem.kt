package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState


data class ModelListItemState(
    val id: Long,
    val headlineText: String,
    val supportingText: String? = null,
    val mediaArtImageState: MediaArtImageState? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelListItem(
    state: ModelListItemState,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background
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
                        text = supportingText,
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
    backgroundColor: Color = MaterialTheme.colorScheme.background
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
    return ModelListItemState(
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