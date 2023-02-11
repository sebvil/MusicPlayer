package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


@OptIn(ExperimentalMaterialApi::class)
@ScreenPreview
@Composable
fun MusicPlayerHostPreview(
    @PreviewParameter(
        MusicPlayerViewStatePreviewParameterProvider::class,
        limit = 1
    ) state: MusicPlayerViewState
) {
    ThemedPreview {
        MusicPlayerHost(
            state = state,
            content = { padding ->
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(50) {
                        ListItem {
                            Text(text = "Item #$it")
                        }
                    }
                }
            },
            onProgressBarClicked = {},
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {},
        )
    }
}