package com.sebastianvm.musicplayer.ui.artist

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.components.HeaderWithImage
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@Composable
fun ArtistScreen(
    screenViewModel: ArtistViewModel,
    navigateToAlbum: (String, String) -> Unit
) {
    val state = screenViewModel.state.observeAsState(screenViewModel.state.value)
    ArtistLayout(state = state.value, delegate = object : ArtistScreenDelegate {
        override fun albumRowClicked(albumGid: String, albumName: String) {
            navigateToAlbum(albumGid, albumName)
        }
    })
}

interface ArtistScreenDelegate : ArtistScreenRowDelegate

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistScreenPreview(@PreviewParameter(ArtistStatePreviewParameterProvider::class) state: ArtistState) {
    ScreenPreview {
        ArtistLayout(state = state, delegate = object : ArtistScreenDelegate {
            override fun albumRowClicked(albumGid: String, albumName: String) = Unit
        })
    }
}

@Composable
fun ArtistLayout(
    state: ArtistState,
    delegate: ArtistScreenDelegate
) {
    with(state) {
        val listWithHeaderState =
            ListWithHeaderState(
                artistHeaderItem,
                (albumsForArtistItems ?: listOf()) + (appearsOnForArtistItems ?: listOf()),
                { s ->
                    HeaderWithImage(
                        state = s,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    )
                },
                { i ->
                    ArtistScreenRow(item = i, delegate = delegate)
                }
            )
        ListWithHeader(state = listWithHeaderState)
    }
}

interface ArtistScreenRowDelegate {
    fun albumRowClicked(albumGid: String, albumName: String)
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistScreenRowPreview(@PreviewParameter(ArtistViewItemProvider::class) item: ArtistScreenItem) {
    ThemedPreview {
        ArtistScreenRow(item = item, delegate = object : ArtistScreenRowDelegate {
            override fun albumRowClicked(albumGid: String, albumName: String) = Unit
        })
    }
}


@Composable
fun ArtistScreenRow(
    @PreviewParameter(ArtistViewItemProvider::class) item: ArtistScreenItem,
    delegate: ArtistScreenRowDelegate
) {
    when (item) {
        is ArtistScreenItem.SectionHeaderItem -> {
            Text(
                text = stringResource(id = item.sectionName),
                modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 8.dp),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        is ArtistScreenItem.AlbumRowItem -> {
            AlbumRow(
                state = item.state,
                modifier = Modifier
                    .clickable {
                        delegate.albumRowClicked(
                            item.albumGid,
                            item.state.albumName
                        )
                    }
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            )
        }
    }
}




