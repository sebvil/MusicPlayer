package com.sebastianvm.musicplayer.ui.library.genres

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class GenreRowItemPreviewParameterProvider :
    PreviewParameterProvider<GenresListItem> {
    override val values = sequenceOf(
        GenresListItem("Pop"),
        GenresListItem("Rock"),
        GenresListItem("Tropipop"),
        GenresListItem("Vallenato")
    )
}

class GenresListStatePreviewParameterProvider :
    PreviewParameterProvider<GenresListState> {
    override val values = sequenceOf(
        GenresListState(
            genresList = listOf(
                GenresListItem("Pop"),
                GenresListItem("Rock"),
                GenresListItem("Tropipop"),
                GenresListItem("Vallenato")
            )
        )
    )
}