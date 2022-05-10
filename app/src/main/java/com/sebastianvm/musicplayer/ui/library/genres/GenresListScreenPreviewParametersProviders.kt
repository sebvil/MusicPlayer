package com.sebastianvm.musicplayer.ui.library.genres

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

class GenresListStatePreviewParameterProvider :
    PreviewParameterProvider<GenresListState> {
    override val values = sequenceOf(
        GenresListState(
            genresList = listOf(
                Genre(id = 0, "Pop"),
                Genre(id = 1,"Rock"),
                Genre(id = 2,"Tropipop"),
                Genre(id = 3,"Vallenato")
            ),
            sortOrder = MediaSortOrder.ASCENDING,
        )
    )
}