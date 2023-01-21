package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavFunction
import com.sebastianvm.musicplayer.ui.navigation.NoArgNavFunction
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold

@Composable
fun AlbumListRoute(
    viewModel: AlbumListViewModel,
    navigateToAlbum: NavFunction<TrackListArguments>,
    openAlbumContextMenu: NavFunction<AlbumContextMenuArguments>,
    openSortMenu: NavFunction<SortMenuArguments>,
    navigateBack: NoArgNavFunction,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    AlbumListScreen(
        state = state,
        navigateToAlbum = navigateToAlbum,
        openAlbumContextMenu = openAlbumContextMenu,
        openSortMenu = openSortMenu,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    state: AlbumListState,
    navigateToAlbum: NavFunction<TrackListArguments>,
    openAlbumContextMenu: NavFunction<AlbumContextMenuArguments>,
    openSortMenu: NavFunction<SortMenuArguments>,
    navigateBack: NoArgNavFunction,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        modifier = modifier,
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.albums),
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        navigateBack()
                    }

                    override fun sortByClicked() {
                        openSortMenu(SortMenuArguments(listType = SortableListType.Albums))
                    }
                }
            )
        }) { paddingValues ->
        AlbumListLayout(
            state = state,
            navigateToAlbum = navigateToAlbum,
            openAlbumContextMenu = openAlbumContextMenu,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AlbumListLayout(
    state: AlbumListState,
    navigateToAlbum: NavFunction<TrackListArguments>,
    openAlbumContextMenu: NavFunction<AlbumContextMenuArguments>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
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
