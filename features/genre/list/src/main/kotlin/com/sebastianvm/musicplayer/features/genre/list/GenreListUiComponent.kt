package com.sebastianvm.musicplayer.features.genre.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.core.designsystems.components.GenreRow
import com.sebastianvm.musicplayer.core.designsystems.components.OverflowIconButton
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.core.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState
import com.sebastianvm.musicplayer.core.ui.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController

class GenreListUiComponent(private val navController: NavController) :
    BaseUiComponent<UiState<GenreListState>, GenreListUserAction, GenreListStateHolder>() {

    override fun createStateHolder(services: Services): GenreListStateHolder {
        return GenreListStateHolder(
            genreRepository = services.repositoryProvider.genreRepository,
            navController = navController,
            sortPreferencesRepository = services.repositoryProvider.sortPreferencesRepository,
            features = services.featureRegistry,
        )
    }

    @Composable
    override fun Content(
        state: UiState<GenreListState>,
        handle: Handler<GenreListUserAction>,
        modifier: Modifier,
    ) {
        GenreList(uiState = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun GenreList(
    uiState: UiState<GenreListState>,
    handle: Handler<GenreListUserAction>,
    modifier: Modifier = Modifier,
) {
    UiStateScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        emptyScreen = {
            StoragePermissionNeededEmptyScreen(
                message = RString.no_genres_found,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            )
        },
    ) { state ->
        GenreList(state = state, handle = handle, modifier = Modifier)
    }
}

@Composable
fun GenreList(
    state: GenreListState,
    handle: Handler<GenreListUserAction>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
        item {
            SortButton(
                state = state.sortButtonState,
                onClick = { handle(GenreListUserAction.SortByButtonClicked) },
                modifier = Modifier.padding(start = 16.dp),
            )
        }
        items(state.genres, key = { item -> item.id }) { item ->
            GenreRow(
                state = item,
                modifier =
                    Modifier.clickable {
                        handle(GenreListUserAction.GenreClicked(item.id, item.genreName))
                    },
                trailingContent = {
                    OverflowIconButton(
                        onClick = { handle(GenreListUserAction.GenreMoreIconClicked(item.id)) }
                    )
                },
            )
        }
    }
}
