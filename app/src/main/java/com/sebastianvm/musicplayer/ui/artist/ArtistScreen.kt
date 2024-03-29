package com.sebastianvm.musicplayer.ui.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.destinations.AlbumContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold

@RootNavGraph
@Destination(navArgsDelegate = ArtistArguments::class)
@Composable
fun ArtistRoute(
    destinationsNavigator: DestinationsNavigator,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()

    UiStateScreen(
        uiState = uiState.toUiState(),
        emptyScreen = {}
    ) { state ->
        ArtistScreen(
            state = state,
            navigateToAlbum = { destinationsNavigator.navigate(TrackListRouteDestination(it)) },
            openAlbumContextMenu = { destinationsNavigator.navigate(AlbumContextMenuDestination(it)) },
            navigateBack = { destinationsNavigator.navigateUp() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    state: ArtistState,
    navigateToAlbum: (TrackListArgumentsForNav) -> Unit,
    openAlbumContextMenu: (AlbumContextMenuArguments) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    ScreenScaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = state.artistName) },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        ArtistLayout(
            state = state,
            navigateToAlbum = navigateToAlbum,
            openAlbumContextMenu = openAlbumContextMenu,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ArtistLayout(
    state: ArtistState,
    navigateToAlbum: (TrackListArgumentsForNav) -> Unit,
    openAlbumContextMenu: (AlbumContextMenuArguments) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
        items(items = state.listItems) { item ->
            when (item) {
                is ArtistScreenItem.SectionHeaderItem -> {
                    ListItem(headlineContent = {
                        Text(
                            text = stringResource(id = item.sectionType.sectionName),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    })
                }

                is ArtistScreenItem.AlbumRowItem -> {
                    ModelListItem(
                        state = item.state,
                        modifier = Modifier.clickable {
                            navigateToAlbum(
                                TrackListArgumentsForNav(
                                    trackListType = MediaGroup.Album(
                                        albumId = item.id
                                    )
                                )
                            )
                        },
                        onMoreClicked = {
                            openAlbumContextMenu(AlbumContextMenuArguments(albumId = item.id))
                        }
                    )
                }
            }
        }
    }
}
