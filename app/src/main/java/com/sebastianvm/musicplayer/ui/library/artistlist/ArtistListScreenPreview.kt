package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


class ArtistListStatePreviewParamsProvider : PreviewParameterProvider<ArtistListState> {
    override val values: Sequence<ArtistListState>
        get() = sequenceOf(ArtistListState(artistList = (1..10).map {
            Artist(
                id = it.toLong(),
                artistName = PreviewUtil.randomString(),
            ).toModelListItemState()
        }))
}

@ScreenPreview
@Composable
private fun ArtistListScreenPreview(@PreviewParameter(ArtistListStatePreviewParamsProvider::class) state: ArtistListState) {
    ScreenPreview {
        ArtistListScreen(
            state = state,
            onSortByClicked = {},
            openArtistContextMenu = {},
            navigateToArtistScreen = {},
            navigateBack = {}
        )
    }
}