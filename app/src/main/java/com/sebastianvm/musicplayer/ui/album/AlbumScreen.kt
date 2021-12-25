package com.sebastianvm.musicplayer.ui.album

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.ui.components.HeaderWithImage
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.components.TrackRow
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
    val minWidth = remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = i.trackNumber?.toString() ?: "",
                            modifier = Modifier
                                .padding(start = AppDimensions.spacing.medium)
                                .defaultMinSize(minWidth = minWidth.value)
                                .onSizeChanged {
                                    with(density) {
                                        if (it.width > minWidth.value.toPx()) {
                                            minWidth.value = it.width.toDp()
                                        }
                                    }
                                }
                        )
                        TrackRow(
                            state = i,
                            modifier = Modifier.clickable {
                                delegate.onTrackClicked(trackId = i.trackId)
                            },
                            onOverflowMenuIconClicked = { delegate.onTrackContextMenuClicked(i.trackId) }
                        )
                    }

                }
            )
        ListWithHeader(state = listWithHeaderState)
    }
}

