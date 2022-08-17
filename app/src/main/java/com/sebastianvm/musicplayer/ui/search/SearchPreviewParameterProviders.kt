package com.sebastianvm.musicplayer.ui.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.repository.fts.SearchMode

class SearchStatePreviewParameterProvider : PreviewParameterProvider<SearchState> {
    override val values = sequenceOf(
        SearchState(
            selectedOption = SearchMode.SONGS,
            searchResults = listOf()
        ),
    )
}