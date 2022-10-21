package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider

class ContextMenuStatePreviewParameterProvider : PreviewParameterProvider<BaseContextMenuState> {
    override val values = sequenceOf(
        AlbumContextMenuState(
            mediaId = 0,
            menuTitle = "La Promesa",
            listItems = listOf(
                ContextMenuItem.Play,
                ContextMenuItem.ViewArtists,
                ContextMenuItem.ViewAlbum
            ),
        )
    )
}

// TODO build tooling for bottom sheet previews
@ComponentPreview
@Composable
fun ContextMenuScreenPreview(@PreviewParameter(ContextMenuStatePreviewParameterProvider::class) state: BaseContextMenuState) {
    ThemedPreview {
        ContextMenuLayout(
            state = state,
            screenDelegate = DefaultScreenDelegateProvider.getDefaultInstance()
        )
    }
}