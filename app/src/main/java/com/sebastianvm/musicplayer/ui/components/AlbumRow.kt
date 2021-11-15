package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


data class AlbumRowState(
    val albumName: String,
    val image: MediaArt,
    val year: Long,
    val artists: String,
)

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumRowPreview(@PreviewParameter(AlbumRowStateProvider::class) state: AlbumRowState) {
    ThemedPreview {
        AlbumRow(state = state)
    }
}

@Composable
fun AlbumRow(state: AlbumRowState, modifier: Modifier = Modifier) {
    with(state) {
        Box(modifier = modifier) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {

                MediaArtImage(
                    image = state.image,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true),
                    iconPadding = PaddingValues(all = 4.dp),
                    contentScale = ContentScale.FillHeight
                )


                Column {
                    Text(
                        text = albumName,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(all = 4.dp)
                    )
                    Row {
                        if (year != 0L) {
                            Text(
                                text = year.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(all = 4.dp)
                            )
                        }
                        Text(
                            text = artists,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(all = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


class AlbumRowStateProvider : PreviewParameterProvider<AlbumRowState> {
    override val values =
        sequenceOf(
            AlbumRowState(
                albumName = "Ahora",
                image = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                ),
                year = 2017,
                artists = "Melendi"

            ),
            AlbumRowState(
                albumName = "VIVES",
                image = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                ),
                year = 2017,
                artists = "Carlos Vives"
            ),
        )
}