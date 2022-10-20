package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil

class GenreListStatePreviewParamProvider : PreviewParameterProvider<GenreListState> {
    override val values: Sequence<GenreListState>
        get() = sequenceOf(GenreListState(genreList = (1..10).map {
            Genre(
                id = it.toLong(),
                genreName = PreviewUtil.randomString(),
            ).toModelListItemState()
        }))
}