package com.sebastianvm.musicplayer.ui.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R
import kotlinx.coroutines.flow.flow

class SearchStatePreviewParameterProvider : PreviewParameterProvider<SearchState> {
    override val values = sequenceOf(
        SearchState(
            selectedOption = R.string.songs,
            trackSearchResults = flow {},
            artistSearchResults = flow {},
            albumSearchResults = flow {},
            genreSearchResults = flow {},
            events = listOf()
        ),
    )
}