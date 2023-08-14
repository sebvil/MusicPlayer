package com.sebastianvm.musicplayer.ui.components.topbar

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.util.compose.StringPreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LibraryTopBarPreview(
    @PreviewParameter(
        StringPreviewParameterProvider::class,
        limit = 3
    ) title: String
) {
    ThemedPreview {
        LibraryTopBar(
            state = LibraryTopBarState(title = title),
            onUpButtonClicked = {},
            titleAlpha = 1f
        )
    }
}
