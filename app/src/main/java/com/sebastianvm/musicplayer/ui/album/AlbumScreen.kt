package com.sebastianvm.musicplayer.ui.album

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.components.HeaderWithImage
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun AlbumScreen(viewModel: AlbumViewModel, navigateToPlayer: () -> Unit) {
    val state = viewModel.state.observeAsState(viewModel.state.value)
    AlbumLayout(
        state = state.value,
        delegate = object : AlbumScreenDelegate {
            override fun trackRowClicked(trackGid: String) {
                viewModel.handle(AlbumUserAction.TrackClicked(trackGid = trackGid))
                navigateToPlayer()
            }
        }
    )
}

interface AlbumScreenDelegate {
    fun trackRowClicked(trackGid: String)
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumScreenPreview(
    @PreviewParameter(AlbumStatePreviewParameterProvider::class) state: AlbumState
) {
    ScreenPreview {
        AlbumLayout(state = state, object : AlbumScreenDelegate {
            override fun trackRowClicked(trackGid: String) = Unit
        })
    }
}


@Composable
fun AlbumLayout(state: AlbumState, delegate: AlbumScreenDelegate) {
    with(state) {
        val listWithHeaderState =
            ListWithHeaderState(
                state.albumHeaderItem,
                albumAdapterItems,
                { s ->
                    HeaderWithImage(
                        state = s,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppDimensions.spacing.mediumLarge),
                    )
                },
                { i ->
                    TrackRow(
                        state = i,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { delegate.trackRowClicked(i.trackGid) }
                            .padding(
                                vertical = AppDimensions.spacing.mediumSmall,
                                horizontal = AppDimensions.spacing.large
                            )
                    )
                }
            )
        ListWithHeader(state = listWithHeaderState)
    }
}

