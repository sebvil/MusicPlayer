package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.BasicSingleLineNoImageModelListItemStatePreviewParameterProvider

class GenreListStatePreviewParamProvider : PreviewParameterProvider<GenreListState> {
    override val values: Sequence<GenreListState>
        get() = sequenceOf(GenreListState(genreList = BasicSingleLineNoImageModelListItemStatePreviewParameterProvider().values.toList()))
}