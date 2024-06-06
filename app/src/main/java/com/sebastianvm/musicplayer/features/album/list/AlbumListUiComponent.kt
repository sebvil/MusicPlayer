package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState

data class AlbumListUiComponent(val navController: NavController) :
    BaseUiComponent<NoArguments, UiState<AlbumListState>, AlbumListUserAction, AlbumListStateHolder>() {
    override val arguments: NoArguments = NoArguments

    @Composable
    override fun Content(
        state: UiState<AlbumListState>,
        handle: Handler<AlbumListUserAction>,
        modifier: Modifier
    ) {
        AlbumList(uiState = state, handle = handle, modifier = modifier)
    }

    override fun createStateHolder(dependencies: AppDependencies): AlbumListStateHolder {
        return getAlbumListStateHolder(dependencies = dependencies, navController = navController)
    }
}

@Composable
fun AlbumList(
    uiState: UiState<AlbumListState>,
    handle: Handler<AlbumListUserAction>,
    modifier: Modifier = Modifier
) {
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
            handle = handle,
        )
    }
}

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
}
