package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.destinations.SortBottomSheetDestination
import com.sebastianvm.musicplayer.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.track.list.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

@Composable
fun AlbumList(
    stateHolder: AlbumListStateHolder,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    val uiState by stateHolder.state.collectAsStateWithLifecycle()
    UiStateScreen(uiState = uiState, modifier = modifier.fillMaxSize(), emptyScreen = {
        StoragePermissionNeededEmptyScreen(
            message = R.string.no_albums_found,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        )
    }) { state ->
        AlbumList(
            state = state,
            handle = stateHolder::handle,
            navigateToAlbum = { args ->
                navigator.navigate(TrackListRouteDestination(args))
            },
            openSortMenu = { args ->
                navigator.navigate(SortBottomSheetDestination(args))
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumList(
    state: AlbumListState,
    handle: Handler<AlbumListUserAction>,
    navigateToAlbum: (TrackListArgumentsForNav) -> Unit,
    openSortMenu: (args: SortMenuArguments) -> Unit,
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
            handle(AlbumListUserAction.AlbumMoreIconClicked(item.id))
        }
    )

    state.albumContextMenuStateHolder?.let { albumContextMenuStateHolder ->
        ModalBottomSheet(
            onDismissRequest = {
                handle(AlbumListUserAction.AlbumContextMenuDismissed)
            },
            windowInsets = WindowInsets(0.dp)
        ) {
            AlbumContextMenu(
                albumContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}
