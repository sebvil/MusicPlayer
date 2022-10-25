package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider

class GenreListStatePreviewParamProvider : PreviewParameterProvider<GenreListState> {
    override val values: Sequence<GenreListState>
        get() = sequenceOf(GenreListState(genreList = (1..10).map {
            Genre(
                id = it.toLong(),
                genreName = PreviewUtil.randomString(),
            ).toModelListItemState()
        }))
}

@ScreenPreview
@Composable
private fun GenreListScreenPreview(@PreviewParameter(GenreListStatePreviewParamProvider::class) state: GenreListState) {
    ScreenPreview {
        GenreListScreen(
            state = state,
            screenDelegate = DefaultScreenDelegateProvider.getDefaultInstance(),
            listState = rememberLazyListState()
        )
    }
}