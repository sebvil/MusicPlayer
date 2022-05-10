package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ContextMenuStatePreviewParameterProvider : PreviewParameterProvider<BaseContextMenuState> {
    override val values = sequenceOf(
        AlbumContextMenuState(
            albumId = 0,
            menuTitle = "La Promesa",
            listItems = listOf(
                ContextMenuItem.Play,
                ContextMenuItem.ViewArtists,
                ContextMenuItem.ViewAlbum
            ),
        )
    )
}