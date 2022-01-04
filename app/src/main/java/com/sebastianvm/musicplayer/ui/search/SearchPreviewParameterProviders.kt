package com.sebastianvm.musicplayer.ui.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R

class SearchStatePreviewParameterProvider : PreviewParameterProvider<SearchState> {
    override val values = sequenceOf(
        SearchState(
            searchTerm = "",
            searchResults = listOf(),
            selectedOption = R.string.songs
        ),
        SearchState(
            searchTerm = "Test",
            searchResults = listOf("Test song"),
            selectedOption = R.string.songs
        )
    )
}