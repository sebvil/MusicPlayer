package com.sebastianvm.musicplayer.ui.playlist

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.Screen

@Composable
fun PlaylistScreen(screenViewModel: PlaylistViewModel) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            TODO("Not yet implemented")
        },
    ) { state ->
        PlaylistLayout(state = state)
    }
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlaylistScreenPreview(@PreviewParameter(PlaylistStatePreviewParameterProvider::class) state: PlaylistState) {
    ScreenPreview {
        PlaylistLayout(state = state)
    }
}

@Composable
fun PlaylistLayout(state: PlaylistState) {
}
