package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

class ArtistRowState(val artistId: String, val artistName: String) : ListItem {
    override val id = artistId
}

fun Artist.toArtistRowState(): ArtistRowState {
    return ArtistRowState(artistId = artistId, artistName = artistName)
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistRowPreview(@PreviewParameter(ArtistRowStatePreviewParameterProvider::class) state: ArtistRowState) {
    ThemedPreview {
        ArtistRow(state = state, delegate = object : ListItemDelegate {})
    }
}

@Composable
fun ArtistRow(state: ArtistRowState, delegate: ListItemDelegate) {
    SingleLineListItem(
//        supportingImage = { modifier ->
//            Surface(
//                color = MaterialTheme.colorScheme.inverseSurface,
//                modifier = modifier.clip(CircleShape)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_artist),
//                    contentDescription = stringResource(id = R.string.placeholder_artist_image),
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .padding(AppDimensions.spacing.xSmall)
//                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
//                )
//            }
//        },
//        supportingImageType = SupportingImageType.AVATAR,
        delegate = delegate
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
        ArtistRowState("Melendi", "Melendi")
    )
}