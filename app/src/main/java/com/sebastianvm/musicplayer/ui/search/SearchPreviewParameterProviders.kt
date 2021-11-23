package com.sebastianvm.musicplayer.ui.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SearchStatePreviewParameterProvider : PreviewParameterProvider<SearchState> {
    override val values = sequenceOf(
        SearchState(
            searchTerm = "",
            searchResults = listOf()
        ),
        SearchState(
            searchTerm = "Test",
            searchResults = listOf("Test song")
        )
    )
}