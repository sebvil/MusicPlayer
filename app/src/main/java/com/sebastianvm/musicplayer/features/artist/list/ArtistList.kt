package com.sebastianvm.musicplayer.features.artist.list

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
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenu
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

@Composable
fun ArtistList(
    stateHolder: ArtistListStateHolder,
    modifier: Modifier = Modifier
) {
    val uiState by stateHolder.currentState
    UiStateScreen(uiState = uiState, modifier = modifier.fillMaxSize(), emptyScreen = {
        StoragePermissionNeededEmptyScreen(
            message = R.string.no_artists_found,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        )
    }) { state ->
        ArtistList(
            state = state,
            handle = stateHolder::handle,
            modifier = Modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistList(
    state: ArtistListState,
    handle: Handler<ArtistListUserAction>,
    modifier: Modifier = Modifier
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = {
            handle(ArtistListUserAction.SortByButtonClicked)
        },
        onItemClicked = { _, item -> handle(ArtistListUserAction.ArtistClicked(item.id)) },
        onItemMoreIconClicked = { _, item ->
            handle(ArtistListUserAction.ArtistMoreIconClicked(item.id))
        }
    )

    state.artistContextMenuStateHolder?.let { artistContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(ArtistListUserAction.ArtistContextMenuDismissed)
            },
        ) {
            ArtistContextMenu(
                stateHolder = artistContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}
