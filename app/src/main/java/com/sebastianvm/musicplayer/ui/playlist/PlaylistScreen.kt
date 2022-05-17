package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.util.compose.ComposePreviews
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun PlaylistScreen(screenViewModel: PlaylistViewModel) {
//    Screen(
//        screenViewModel = screenViewModel,
//        eventHandler = { event ->
//            TODO("Not yet implemented")
//        },
//    ) { state ->
//        PlaylistLayout(state = state)
//    }
}


@ComposePreviews
@Composable
fun PlaylistScreenPreview(@PreviewParameter(PlaylistStatePreviewParameterProvider::class) state: PlaylistState) {
    ScreenPreview {
        PlaylistLayout(state = state)
    }
}

@Composable
fun PlaylistLayout(state: PlaylistState) {
}
