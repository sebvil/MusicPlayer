package com.sebastianvm.musicplayer.ui.album

import android.content.res.Configuration
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
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface AlbumNavigationDelegate {
    fun navigateToPlayer() = Unit
    fun openContextMenu(trackId: String, albumId: String) = Unit
}

@Composable
fun AlbumScreen(
    screenVieModel: AlbumViewModel,
    delegate: AlbumNavigationDelegate
) {
    Screen(
        screenViewModel = screenVieModel,
        eventHandler = { event ->
            when (event) {
                is AlbumUiEvent.NavigateToPlayer -> {
                    delegate.navigateToPlayer()
                }
                is AlbumUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(trackId = event.trackId, albumId = event.albumId)
                }
            }
        }) { state ->
        AlbumLayout(state = state, delegate = object : AlbumScreenDelegate {
            override fun onTrackClicked(trackId: String) {
                screenVieModel.handle(AlbumUserAction.TrackClicked(trackId = trackId))
            }

            override fun onTrackContextMenuClicked(trackId: String) {
                screenVieModel.handle(AlbumUserAction.TrackContextMenuClicked(trackId = trackId))
            }
        })
    }
}

interface AlbumScreenDelegate {
    fun onTrackClicked(trackId: String) = Unit
    fun onTrackContextMenuClicked(trackId: String) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumScreenPreview(
    @PreviewParameter(AlbumStatePreviewParameterProvider::class) state: AlbumState
) {
    ScreenPreview {
        AlbumLayout(state = state, delegate = object : AlbumScreenDelegate {})
    }
}


@Composable
fun AlbumLayout(state: AlbumState, delegate: AlbumScreenDelegate) {
    with(state) {
        val listWithHeaderState =
            ListWithHeaderState(
                state.albumHeaderItem,
                tracksList,
                { s ->
                    HeaderWithImage(
                        state = s,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppDimensions.spacing.medium),
                    )
                },
                { i ->
                    TrackRow(
                        state = i,
                        delegate = object : ListItemDelegate {
                            override fun onItemClicked() {
                                delegate.onTrackClicked(i.trackId)
                            }

                            override fun onSecondaryActionIconClicked() {
                                delegate.onTrackContextMenuClicked(i.trackId)
                            }
                        }
                    )
                }
            )
        ListWithHeader(state = listWithHeaderState)
    }
}

