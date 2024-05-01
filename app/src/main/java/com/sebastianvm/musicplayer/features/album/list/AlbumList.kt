package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.components.BottomSheet
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.sort.SortMenu
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

@Composable
fun AlbumList(
    stateHolder: AlbumListStateHolder,
    modifier: Modifier = Modifier
) {
    val uiState by stateHolder.currentState
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
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumList(
    state: AlbumListState,
    handle: Handler<AlbumListUserAction>,
    modifier: Modifier = Modifier
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = {
            handle(AlbumListUserAction.SortButtonClicked)
        },
        onItemClicked = { _, item ->
            handle(AlbumListUserAction.AlbumClicked(item.id))
        },
        onItemMoreIconClicked = { _, item ->
            handle(AlbumListUserAction.AlbumMoreIconClicked(item.id))
        }
    )

    state.albumContextMenuStateHolder?.let { albumContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(AlbumListUserAction.AlbumContextMenuDismissed)
            },
        ) {
            AlbumContextMenu(
                stateHolder = albumContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }

    state.sortMenuStateHolder?.let { sortMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(AlbumListUserAction.SortMenuDismissed)
            },
        ) {
            SortMenu(
                stateHolder = sortMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}
