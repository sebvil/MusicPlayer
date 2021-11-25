package com.sebastianvm.musicplayer.ui.artist

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.components.HeaderWithImage
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@Composable
fun ArtistScreen(
    screenViewModel: ArtistViewModel,
    navigateToAlbum: (String, String) -> Unit
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is ArtistUiEvent.NavigateToAlbum -> {
                    navigateToAlbum(event.albumGid, event.albumName)
                }
            }
        }
    ) { state ->
        ArtistLayout(state = state, delegate = object : ArtistScreenDelegate {
            override fun albumRowClicked(albumGid: String, albumName: String) {
                screenViewModel.handle(
                    ArtistUserAction.AlbumClicked(
                        albumGid = albumGid,
                        albumName = albumName
                    )
                )
            }
        })
    }
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
                            .padding(vertical = AppDimensions.spacing.mediumLarge),
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
                modifier = Modifier.padding(
                    start = AppDimensions.spacing.large,
                    end = AppDimensions.spacing.large,
                    bottom = AppDimensions.spacing.mediumSmall
                ),
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
                    .padding(
                        horizontal = AppDimensions.spacing.large,
                        vertical = AppDimensions.spacing.mediumSmall
                    )
            )
        }
    }
}




