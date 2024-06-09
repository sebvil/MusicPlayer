package com.sebastianvm.musicplayer.features.genre.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.designsystem.components.GenreRow
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.util.resources.RString

data class GenreListUiComponent(val navController: NavController) :
    BaseUiComponent<
        NoArguments,
        UiState<GenreListState>,
        GenreListUserAction,
        GenreListStateHolder,
    >() {
    override val arguments: NoArguments = NoArguments

    override fun createStateHolder(dependencies: Dependencies): GenreListStateHolder {
        return getGenreListStateHolder(dependencies = dependencies, navController = navController)
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
                onMoreIconClicked = { handle(GenreListUserAction.GenreMoreIconClicked(item.id)) },
                modifier = Modifier.clickable { handle(GenreListUserAction.GenreClicked(item.id)) },
            )
        }
    }
}
