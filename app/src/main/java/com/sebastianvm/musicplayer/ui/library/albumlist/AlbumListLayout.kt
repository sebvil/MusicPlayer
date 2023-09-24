package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav

@Composable
fun AlbumListLayout(
    state: AlbumListState,
    navigateToAlbum: (TrackListArgumentsForNav) -> Unit,
    openSortMenu: (args: SortMenuArguments) -> Unit,
    openAlbumContextMenu: (AlbumContextMenuArguments) -> Unit,
    modifier: Modifier = Modifier
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = {
            openSortMenu(SortMenuArguments(listType = SortableListType.Albums))
        },
        onItemClicked = { _, item ->
            navigateToAlbum(
                TrackListArgumentsForNav(
                    trackListType = MediaGroup.Album(
                        item.id
                    )
                )
            )
        },
        onItemMoreIconClicked = { _, item ->
            openAlbumContextMenu(
                AlbumContextMenuArguments(item.id)
            )
        }
    )
}
