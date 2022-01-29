package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

data class ArtistRowState(val artistName: String, val shouldShowContextMenu: Boolean) : ListItem {
    override val id = artistName
}

fun Artist.toArtistRowState(shouldShowContextMenu: Boolean = false): ArtistRowState {
    return ArtistRowState(artistName = artistName, shouldShowContextMenu = shouldShowContextMenu)
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistRowPreview(@PreviewParameter(ArtistRowStatePreviewParameterProvider::class) state: ArtistRowState) {
    ThemedPreview {
        ArtistRow(state = state) {}
    }
}

@Composable
fun ArtistRow(
    state: ArtistRowState,
    modifier: Modifier = Modifier,
    onOverflowMenuIconClicked: () -> Unit = {}
) {
    SingleLineListItem(
        modifier = modifier,
        afterListContent = {
            if (state.shouldShowContextMenu) {
                IconButton(
                    onClick = onOverflowMenuIconClicked,
                    modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_overflow),
                        contentDescription = stringResource(R.string.more)
                    )
                }
            }
        }
    ) {
        Text(
            text = state.artistName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

class ArtistRowStatePreviewParameterProvider :
    PreviewParameterProvider<ArtistRowState> {
    override val values = sequenceOf(
        ArtistRowState(artistName = "Melendi", shouldShowContextMenu = true),
        ArtistRowState(artistName = "Morat", shouldShowContextMenu = false)
    )
}
