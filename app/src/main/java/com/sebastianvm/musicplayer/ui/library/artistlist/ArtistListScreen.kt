package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelList

@Composable
fun ArtistListLayout(
    state: ArtistListState,
    openArtistContextMenu: (ArtistContextMenuArguments) -> Unit,
    navigateToArtistScreen: (ArtistArguments) -> Unit,
    changeSort: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = changeSort,
        onItemClicked = { _, item -> navigateToArtistScreen(ArtistArguments(item.id)) },
        onItemMoreIconClicked = { _, item ->
            openArtistContextMenu(
                ArtistContextMenuArguments(item.id)
            )
        }
    )
}
