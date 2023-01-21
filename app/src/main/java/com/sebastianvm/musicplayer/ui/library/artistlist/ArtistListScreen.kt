package com.sebastianvm.musicplayer.ui.library.artistlist

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
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold

@Composable
fun ArtistListRoute(
    viewModel: ArtistListViewModel,
    openArtistContextMenu: (ArtistContextMenuArguments) -> Unit,
    navigateToArtistScreen: (ArtistArguments) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    ArtistListScreen(
        state = state,
        onSortByClicked = { viewModel.handle(ArtistListUserAction.SortByButtonClicked) },
        openArtistContextMenu = openArtistContextMenu,
        navigateToArtistScreen = navigateToArtistScreen,
        navigateBack = navigateBack,
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistListScreen(
    state: ArtistListState,
    onSortByClicked: () -> Unit,
    openArtistContextMenu: (ArtistContextMenuArguments) -> Unit,
    navigateToArtistScreen: (ArtistArguments) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        modifier = modifier,
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.artists),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        onSortByClicked()
                    }

                    override fun upButtonClicked() {
                        navigateBack()
                    }
                })
        }
    ) { paddingValues ->
        ArtistListLayout(
            state = state,
            openArtistContextMenu = openArtistContextMenu,
            navigateToArtistScreen = navigateToArtistScreen,
            modifier = Modifier.padding(paddingValues)
        )
    }
}


@Composable
fun ArtistListLayout(
    state: ArtistListState,
    openArtistContextMenu: (ArtistContextMenuArguments) -> Unit,
    navigateToArtistScreen: (ArtistArguments) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(state.artistList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    navigateToArtistScreen(ArtistArguments(item.id))
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            openArtistContextMenu(ArtistContextMenuArguments(artistId = item.id))
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
