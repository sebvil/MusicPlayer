package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class GenreListStatePreviewParameterProvider :
    PreviewParameterProvider<GenreListState> {
    override val values = sequenceOf(
        GenreListState(
            genreList = listOf(
                ModelListItemState(id = 0, "Pop"),
                ModelListItemState(id = 1, "Rock"),
                ModelListItemState(id = 2, "Tropipop"),
                ModelListItemState(id = 3, "Vallenato")
            ),
        )
    )
}