package com.sebastianvm.musicplayer.features.artist.list

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


data class ArtistListUiComponent(val navController: NavController) :
    BaseUiComponent<NoArguments, ArtistListStateHolder>() {
    override val arguments: NoArguments = NoArguments

    @Composable
    override fun Content(stateHolder: ArtistListStateHolder, modifier: Modifier) {
        ArtistList(stateHolder = stateHolder, modifier = modifier)
    }

    override fun createStateHolder(dependencies: DependencyContainer): ArtistListStateHolder {
        return getArtistListStateHolder(dependencies = dependencies, navController = navController)
    }
}


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
}
