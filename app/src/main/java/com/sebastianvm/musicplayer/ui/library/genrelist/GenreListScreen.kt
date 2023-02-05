package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavFunction
import com.sebastianvm.musicplayer.ui.navigation.NoArgNavFunction
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold

@Composable
fun GenreListRoute(
    viewModel: GenreListViewModel,
    navigateToGenre: NavFunction<TrackListArguments>,
    openGenreContextMenu: NavFunction<GenreContextMenuArguments>,
    navigateBack: NoArgNavFunction,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    GenreListScreen(
        state = state,
        onSortClicked = { viewModel.handle(GenreListUserAction.SortByButtonClicked) },
        navigateToGenre = navigateToGenre,
        openGenreContextMenu = openGenreContextMenu,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreListScreen(
    state: GenreListState,
    onSortClicked: () -> Unit,
    navigateToGenre: NavFunction<TrackListArguments>,
    openGenreContextMenu: NavFunction<GenreContextMenuArguments>,
    navigateBack: NoArgNavFunction,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        modifier = modifier,
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.genres),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        onSortClicked()
                    }

                    override fun upButtonClicked() {
                        navigateBack()
                    }
                })
        }) { paddingValues ->
        GenreListLayout(
            state = state,
            navigateToGenre = navigateToGenre,
            openGenreContextMenu = openGenreContextMenu,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun GenreListLayout(
    state: GenreListState,
    navigateToGenre: NavFunction<TrackListArguments>,
    openGenreContextMenu: NavFunction<GenreContextMenuArguments>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(state.genreList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    navigateToGenre(
                        TrackListArguments(trackList = MediaGroup.Genre(genreId = item.id))
                    )

                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            openGenreContextMenu(GenreContextMenuArguments(genreId = item.id))
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(id = R.string.more)
                        )
                    }
                }
            )
        }
    }
}
