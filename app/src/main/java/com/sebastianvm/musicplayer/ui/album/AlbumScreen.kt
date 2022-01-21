package com.sebastianvm.musicplayer.ui.album

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
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
        LazyColumn {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimensions.spacing.medium),
                ) {
                    MediaArtImage(
                        uri = state.imageUri,
                        contentDescription = stringResource(
                            id = R.string.album_art_for_album,
                            state.albumName
                        ),
                        backupContentDescription = R.string.placeholder_album_art,
                        backupResource = R.drawable.ic_album,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(all = AppDimensions.spacing.mediumLarge),
                        contentScale = ContentScale.FillHeight
                    )
                    Text(
                        text = state.albumName,
                        modifier = Modifier.padding(horizontal = AppDimensions.spacing.mediumLarge),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            items(tracksList) { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.trackNumber?.toString() ?: "",
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
                        state = item,
                        modifier = Modifier.clickable {
                            delegate.onTrackClicked(trackId = item.trackId)
                        },
                        onOverflowMenuIconClicked = { delegate.onTrackContextMenuClicked(item.trackId) }
                    )
                }
            }
        }
    }
}
