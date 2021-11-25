package com.sebastianvm.musicplayer.ui.album

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.components.HeaderWithImage
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun AlbumScreen(screenVieModel: AlbumViewModel, navigateToPlayer: () -> Unit) {
    Screen(
        screenViewModel = screenVieModel,
        eventHandler = { event ->
            when (event) {
                is AlbumUiEvent.NavigateToPlayer -> {
                    navigateToPlayer()
                }
            }
        }) { state ->
        AlbumLayout(state = state) { trackGid ->
            screenVieModel.handle(AlbumUserAction.TrackClicked(trackGid = trackGid))
        }
    }
}

typealias OnTrackRowClicked = (trackGid: String) -> Unit

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumScreenPreview(
    @PreviewParameter(AlbumStatePreviewParameterProvider::class) state: AlbumState
) {
    ScreenPreview {
        AlbumLayout(state = state) {}
    }
}


@Composable
fun AlbumLayout(state: AlbumState, onTrackRowClicked: OnTrackRowClicked) {
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
                            .clickable { onTrackRowClicked(i.trackGid) }
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

