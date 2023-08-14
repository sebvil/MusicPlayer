package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav

@Composable
fun AlbumListLayout(
    state: AlbumListState,
    navigateToAlbum: (TrackListArgumentsForNav) -> Unit,
    openAlbumContextMenu: (AlbumContextMenuArguments) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.albumList.isEmpty()) {
        StoragePermissionNeededEmptyScreen(
            message = R.string.no_albums_found,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        )
    } else {
        LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
            items(state.albumList) { item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier.clickable {
                        navigateToAlbum(
                            TrackListArgumentsForNav(
                                trackListType = MediaGroup.Album(
                                    albumId = item.id
                                )
                            )
                        )
                    },
                    onMoreClicked = {
                        openAlbumContextMenu(AlbumContextMenuArguments(item.id))
                    }
                )
            }
        }
    }
}
