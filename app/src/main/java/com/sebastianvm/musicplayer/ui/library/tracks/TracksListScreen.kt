package com.sebastianvm.musicplayer.ui.library.tracks

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.ui.components.LibraryTitle
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun TracksListScreen(
    screenViewModel: TracksListViewModel = viewModel(),
    navigateToPlayer: () -> Unit,
) {
    val state = screenViewModel.state.observeAsState(screenViewModel.state.value)
    TracksListLayout(state = state.value, delegate = object : TracksListScreenDelegate {
        override fun onTrackClicked(trackGid: String) {
            screenViewModel.handle(
                TracksListUserAction.TrackClicked(
                    trackGid
                )
            )
            navigateToPlayer()
        }
    })
}

interface TracksListScreenDelegate {
    fun onTrackClicked(trackGid: String)
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TracksListScreenPreview(
    @PreviewParameter(TracksListStatePreviewParameterProvider::class) state: TracksListState
) {
    ScreenPreview {
        TracksListLayout(state = state, delegate = object : TracksListScreenDelegate {
            override fun onTrackClicked(trackGid: String) = Unit
        })
    }
}

@Composable
fun TracksListLayout(
    state: TracksListState,
    delegate: TracksListScreenDelegate
) {
    val listWithHeaderState =
        ListWithHeaderState(
            state.tracksListTitle,
            state.tracksList,
            { s -> LibraryTitle(title = s) },
            { i ->
                TrackRow(
                    state = i,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { delegate.onTrackClicked(i.trackGid) }
                        .padding(
                            vertical = AppDimensions.spacing.mediumSmall,
                            horizontal = AppDimensions.spacing.large
                        )
                )
            }
        )
    ListWithHeader(state = listWithHeaderState)
}
