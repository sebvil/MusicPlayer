package com.sebastianvm.musicplayer.ui.library.genres

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.util.SortOrder

class GenresListStatePreviewParameterProvider :
    PreviewParameterProvider<GenresListState> {
    override val values = sequenceOf(
        GenresListState(
            genresList = listOf(
                Genre("Pop"),
                Genre("Rock"),
                Genre("Tropipop"),
                Genre("Vallenato")
            ),
            sortOrder = SortOrder.ASCENDING
        )
    )
}