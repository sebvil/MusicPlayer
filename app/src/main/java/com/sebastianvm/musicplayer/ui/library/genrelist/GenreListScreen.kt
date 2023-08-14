package com.sebastianvm.musicplayer.ui.library.genrelist


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav

@Composable
fun GenreListLayout(
    state: GenreListState,
    navigateToGenre: (TrackListArgumentsForNav) -> Unit,
    openGenreContextMenu: (GenreContextMenuArguments) -> Unit,
    changeSort: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = changeSort,
        onItemClicked = { _, item ->
            navigateToGenre(
                TrackListArgumentsForNav(
                    trackListType = MediaGroup.Genre(
                        item.id
                    )
                )
            )
        },
        onItemMoreIconClicked = { _, item ->
            openGenreContextMenu(
                GenreContextMenuArguments(item.id)
            )
        }
    )
}
