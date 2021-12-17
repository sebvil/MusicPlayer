package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ContextMenuStatePreviewParameterProvider : PreviewParameterProvider<ContextMenuState> {
    override val values = sequenceOf(
        ContextMenuState(
            listItems = listOf(
                ContextMenuItem.Play,
                ContextMenuItem.ViewArtists,
                ContextMenuItem.ViewAlbums
            )
        )
    )
}