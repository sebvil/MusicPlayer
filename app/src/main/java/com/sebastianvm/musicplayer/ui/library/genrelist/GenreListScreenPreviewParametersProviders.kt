package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class GenreListStatePreviewParameterProvider :
    PreviewParameterProvider<GenreListState> {
    override val values = sequenceOf(
        GenreListState(
            genreList = listOf(
                ModelListItemState.Basic(id = 0, "Pop"),
                ModelListItemState.Basic(id = 1, "Rock"),
                ModelListItemState.Basic(id = 2, "Tropipop"),
                ModelListItemState.Basic(id = 3, "Vallenato")
            ),
        )
    )
}