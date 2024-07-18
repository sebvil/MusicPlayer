package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.OverflowIconButton
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.core.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.mvvm.NoArguments
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController

data class AlbumListUiComponent(val navController: NavController) :
    BaseUiComponent<
        NoArguments,
        UiState<AlbumListState>,
        AlbumListUserAction,
        AlbumListStateHolder,
    >() {
    override val arguments: NoArguments = NoArguments

    @Composable
    override fun Content(
        state: UiState<AlbumListState>,
        handle: Handler<AlbumListUserAction>,
        modifier: Modifier,
    ) {
        AlbumList(uiState = state, handle = handle, modifier = modifier)
    }

    override fun createStateHolder(dependencies: Dependencies): AlbumListStateHolder {
        return AlbumListStateHolder(
            albumRepository = dependencies.repositoryProvider.albumRepository,
            navController = navController,
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
        )
    }
}

@Composable
fun AlbumList(
    uiState: UiState<AlbumListState>,
    handle: Handler<AlbumListUserAction>,
    modifier: Modifier = Modifier,
) {
    UiStateScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        emptyScreen = {
            StoragePermissionNeededEmptyScreen(
                message = RString.no_albums_found,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            )
        },
    ) { state ->
        AlbumList(state = state, handle = handle)
    }
}

@Composable
fun AlbumList(
    state: AlbumListState,
    handle: Handler<AlbumListUserAction>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
        item {
            SortButton(
                state = state.sortButtonState,
                onClick = { handle(AlbumListUserAction.SortButtonClicked) },
                modifier = Modifier.padding(start = 16.dp),
            )
        }
        items(state.albums, key = { item -> item.id }) { item ->
            AlbumRow(
                state = item,
                modifier = Modifier.clickable { handle(AlbumListUserAction.AlbumClicked(item)) },
                trailingContent = {
                    OverflowIconButton(
                        onClick = { handle(AlbumListUserAction.AlbumMoreIconClicked(item.id)) }
                    )
                },
            )
        }
    }
}
