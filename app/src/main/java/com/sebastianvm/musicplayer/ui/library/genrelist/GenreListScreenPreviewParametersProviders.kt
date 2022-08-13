package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.database.entities.Genre

class GenreListStatePreviewParameterProvider :
    PreviewParameterProvider<GenreListState> {
    override val values = sequenceOf(
        GenreListState(
            genreList = listOf(
                Genre(id = 0, "Pop"),
                Genre(id = 1, "Rock"),
                Genre(id = 2, "Tropipop"),
                Genre(id = 3, "Vallenato")
            ),
        )
    )
}