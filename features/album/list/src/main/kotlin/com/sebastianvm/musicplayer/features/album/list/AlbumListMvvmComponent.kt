package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.OverflowIconButton
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.core.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseMvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState
import com.sebastianvm.musicplayer.core.ui.mvvm.viewModels
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

data class AlbumListMvvmComponent(
    private val navController: NavController,
    private val albumRepository: AlbumRepository,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val features: FeatureRegistry,
) : BaseMvvmComponent<UiState<AlbumListState>, AlbumListUserAction, AlbumListViewModel>() {

    @Composable
    override fun Content(
        state: UiState<AlbumListState>,
        handle: Handler<AlbumListUserAction>,
        modifier: Modifier,
    ) {
        AlbumList(uiState = state, handle = handle, modifier = modifier)
    }

    override val viewModel: AlbumListViewModel by viewModels {
        AlbumListViewModel(
            albumRepository = albumRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            navController = navController,
            features = features,
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
