package com.sebastianvm.musicplayer.features.genre.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

data class GenreListUiComponent(val navController: NavController) :
    BaseUiComponent<NoArguments, GenreListStateHolder>() {
    override val arguments: NoArguments = NoArguments

    @Composable
    override fun Content(stateHolder: GenreListStateHolder, modifier: Modifier) {
        GenreList(stateHolder = stateHolder, modifier = modifier)
    }

    override fun createStateHolder(dependencies: DependencyContainer): GenreListStateHolder {
        return getGenreListStateHolder(dependencies = dependencies, navController = navController)
    }
}

@Composable
fun GenreList(
    stateHolder: GenreListStateHolder,
    modifier: Modifier = Modifier
) {
    val uiState by stateHolder.currentState
    UiStateScreen(uiState = uiState, modifier = modifier.fillMaxSize(), emptyScreen = {
        StoragePermissionNeededEmptyScreen(
            message = R.string.no_genres_found,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        )
    }) { state ->
        GenreList(
            state = state,
            handle = stateHolder::handle,
            modifier = Modifier
        )
    }
}

@Composable
fun GenreList(
    state: GenreListState,
    handle: Handler<GenreListUserAction>,
    modifier: Modifier = Modifier
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = {
            handle(GenreListUserAction.SortByButtonClicked)
        },
        onItemClicked = { _, item ->
            handle(GenreListUserAction.GenreClicked(item.id))
        },
        onItemMoreIconClicked = { _, item ->
            handle(GenreListUserAction.GenreMoreIconClicked(item.id))
        }
    )
}
