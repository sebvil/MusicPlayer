package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class ArtistsBottomSheetStatePreviewParameterProvider :
    PreviewParameterProvider<ArtistsBottomSheetState> {
    override val values = sequenceOf(
        ArtistsBottomSheetState(
            artistList = listOf(
                ModelListItemState.Basic(id = 0, headlineContent = "Melendi"),
                ModelListItemState.Basic(id = 1, headlineContent = "Carlos Vives")
            ),
        )
    )
}


///**
// * The Android Studio Preview cannot handle this, but it can be run in device for preview
// */
//@Preview
//@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun ArtistsBottomSheetLayoutPreview(
//    @PreviewParameter(
//        ArtistsBottomSheetStatePreviewParameterProvider::class
//    ) state: ArtistsBottomSheetState
//) {
//    BottomSheetPreview {
//        ArtistsBottomSheetLayout(
//            state = state,
//            screenDelegate = DefaultScreenDelegateProvider.getDefaultInstance()
//        )
//    }
//}