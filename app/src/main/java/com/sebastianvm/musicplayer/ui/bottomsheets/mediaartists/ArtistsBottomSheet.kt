package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.util.compose.BottomSheetPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import kotlinx.coroutines.Dispatchers

@Composable
fun ArtistsBottomSheetDialog(sheetViewModel: ArtistsBottomSheetViewModel) {
    val state = sheetViewModel.state.collectAsState(context = Dispatchers.Main)
    HandleEvents(eventsFlow = sheetViewModel.eventsFlow) { event ->
        TODO("Not yet implemented")
    }
    ArtistsBottomSheetLayout(state = state.value)
}


/**
 * The Android Studio Preview cannot handle this, but it can be run in device for preview
 */
@Preview
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistsScreenPreview(@PreviewParameter(ArtistsStatePreviewParameterProvider::class) state: ArtistsBottomSheetState) {
    BottomSheetPreview {
        ArtistsBottomSheetLayout(state = state)
    }
}

@Composable
fun ArtistsBottomSheetLayout(state: ArtistsBottomSheetState) {
}
