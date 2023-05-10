package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavFunction

@Composable
fun AlbumListLayout(
    state: AlbumListState,
    navigateToAlbum: NavFunction<TrackListArguments>,
    openAlbumContextMenu: NavFunction<AlbumContextMenuArguments>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
        items(state.albumList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    navigateToAlbum(TrackListArguments(trackList = MediaGroup.Album(albumId = item.id)))
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            openAlbumContextMenu(AlbumContextMenuArguments(item.id))
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(id = R.string.more)
                        )
                    }
                }
            )
        }
    }
}
