package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.util.compose.BottomSheetPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider

class ArtistsBottomSheetStatePreviewParameterProvider :
    PreviewParameterProvider<ArtistsBottomSheetState> {
    override val values = sequenceOf(
        ArtistsBottomSheetState(
            artistList = listOf(
                ModelListItemState.Basic(id = 0, headlineText = "Melendi"),
                ModelListItemState.Basic(id = 1, headlineText = "Carlos Vives")
            ),
            artistIds = listOf(0, 1)
        )
    )
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
        ArtistsBottomSheetLayout(
            state = state,
            screenDelegate = DefaultScreenDelegateProvider.getDefaultInstance()
        )
    }
}