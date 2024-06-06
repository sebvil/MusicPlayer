package com.sebastianvm.musicplayer.features.artist.list

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

data class ArtistListUiComponent(val navController: NavController) :
    BaseUiComponent<
        NoArguments,
        UiState<ArtistListState>,
        ArtistListUserAction,
        ArtistListStateHolder,
    >() {
    override val arguments: NoArguments = NoArguments

    override fun createStateHolder(dependencies: AppDependencies): ArtistListStateHolder {
        return getArtistListStateHolder(dependencies = dependencies, navController = navController)
    }

    @Composable
    override fun Content(
        state: UiState<ArtistListState>,
        handle: Handler<ArtistListUserAction>,
        modifier: Modifier,
    ) {
        ArtistList(uiState = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun ArtistList(
    uiState: UiState<ArtistListState>,
    handle: Handler<ArtistListUserAction>,
    modifier: Modifier = Modifier,
) {
    UiStateScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        emptyScreen = {
            StoragePermissionNeededEmptyScreen(
                message = R.string.no_artists_found,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            )
        },
    ) { state ->
        ArtistList(state = state, handle = handle, modifier = Modifier)
    }
}

@Composable
fun ArtistList(
    state: ArtistListState,
    handle: Handler<ArtistListUserAction>,
    modifier: Modifier = Modifier,
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = { handle(ArtistListUserAction.SortByButtonClicked) },
        onItemClicked = { _, item -> handle(ArtistListUserAction.ArtistClicked(item.id)) },
        onItemMoreIconClicked = { _, item ->
            handle(ArtistListUserAction.ArtistMoreIconClicked(item.id))
        },
    )
}
