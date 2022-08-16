package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.BottomSheetPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleNavEvents
import kotlinx.coroutines.Dispatchers

// TODO fix bottom sheet colors
@Composable
fun ArtistsBottomSheet(
    sheetViewModel: ArtistsBottomSheetViewModel,
    navigationDelegate: NavigationDelegate
) {
    val state = sheetViewModel.state.collectAsState(context = Dispatchers.Main)
    HandleEvents(viewModel = sheetViewModel) {}
    HandleNavEvents(viewModel = sheetViewModel, navigationDelegate = navigationDelegate)
    ArtistsBottomSheetLayout(state = state.value, delegate = object : ArtistsBottomSheetDelegate {
        override fun onArtistRowClicked(artistId: Long) {
            sheetViewModel.onArtistClicked(artistId)
        }
    })
}

interface ArtistsBottomSheetDelegate {
    fun onArtistRowClicked(artistId: Long) = Unit
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.bottomSheet.rowHeight)
                    .padding(start = AppDimensions.bottomSheet.startPadding),
            ) {

                Text(
                    text = stringResource(id = R.string.artists),
                    modifier = Modifier.paddingFromBaseline(top = 36.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
        items(state.artistList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    delegate.onArtistRowClicked(item.id)
                }
            )

        }
    }
}
