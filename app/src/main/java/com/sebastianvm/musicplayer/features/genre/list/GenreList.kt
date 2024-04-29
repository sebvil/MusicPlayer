package com.sebastianvm.musicplayer.features.genre.list

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenu
import com.sebastianvm.musicplayer.features.track.list.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

@Composable
fun GenreList(
    stateHolder: GenreListStateHolder,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    val uiState by stateHolder.state.collectAsStateWithLifecycle()
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
            navigateToGenreScreen = { args ->
                navigator.navigate(TrackListRouteDestination(args))
            },
            modifier = Modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreList(
    state: GenreListState,
    handle: Handler<GenreListUserAction>,
    navigateToGenreScreen: (TrackListArgumentsForNav) -> Unit,
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
            navigateToGenreScreen(
                TrackListArgumentsForNav(
                    MediaGroup.Genre(
                        item.id
                    )
                )
            )
        },
        onItemMoreIconClicked = { _, item ->
            handle(GenreListUserAction.GenreMoreIconClicked(item.id))
        }
    )

    state.genreContextMenuStateHolder?.let { genreContextMenuStateHolder ->
        ModalBottomSheet(
            onDismissRequest = {
                handle(GenreListUserAction.GenreContextMenuDismissed)
            },
            windowInsets = WindowInsets(0.dp)
        ) {
            GenreContextMenu(
                stateHolder = genreContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}
