package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class AlbumListStatePreviewParameterProvider : PreviewParameterProvider<AlbumListState> {
    override val values = sequenceOf(
        AlbumListState(
            albumList = listOf()
        )
    )
}
