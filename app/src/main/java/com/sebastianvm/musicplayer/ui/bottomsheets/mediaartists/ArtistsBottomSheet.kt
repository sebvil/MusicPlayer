package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.ArtistRow
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.BottomSheetPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import kotlinx.coroutines.Dispatchers

interface ArtistsBottomSheetNavigationDelegate {
    fun navigateToArtist(artistId: String) = Unit
}

@Composable
fun ArtistsBottomSheet(
    sheetViewModel: ArtistsBottomSheetViewModel,
    delegate: ArtistsBottomSheetNavigationDelegate
) {
    val state = sheetViewModel.state.collectAsState(context = Dispatchers.Main)
    HandleEvents(eventsFlow = sheetViewModel.eventsFlow) { event ->
        when (event) {
            is ArtistsBottomSheetUiEvent.NavigateToArtist -> {
                delegate.navigateToArtist(artistId = event.artistId)
            }
        }
    }
    ArtistsBottomSheetLayout(state = state.value, delegate = object : ArtistsBottomSheetDelegate {
        override fun onArtistRowClicked(artistId: String) {
            sheetViewModel.handle(ArtistsBottomSheetUserAction.ArtistClicked(artistId))
        }
    })
}

interface ArtistsBottomSheetDelegate {
    fun onArtistRowClicked(artistId: String) = Unit
}


/**
 * The Android Studio Preview cannot handle this, but it can be run in device for preview
 */
@Preview
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistsBottomSheetLayoutPreview(
    @PreviewParameter(
        ArtistsBottomSheetStatePreviewParameterProvider::class
    ) state: ArtistsBottomSheetState
) {
    BottomSheetPreview {
        ArtistsBottomSheetLayout(state = state, delegate = object : ArtistsBottomSheetDelegate {})
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistsBottomSheetLayout(state: ArtistsBottomSheetState, delegate: ArtistsBottomSheetDelegate) {
    LazyColumn {
        stickyHeader {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.bottomSheet.rowHeight)
                    .padding(start = AppDimensions.bottomSheet.startPadding)
            ) {

                Text(
                    text = stringResource(id = R.string.artists),
                    modifier = Modifier.paddingFromBaseline(top = 36.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
        items(state.artistsList) { item ->
            ArtistRow(state = item, delegate = object : ListItemDelegate {
                override fun onItemClicked() {
                    delegate.onArtistRowClicked(item.artistGid)
                }
            })
        }
    }
}
