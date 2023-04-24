package com.sebastianvm.musicplayer.ui.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavFunction
import com.sebastianvm.musicplayer.ui.navigation.NoArgNavFunction
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold


@Composable
fun ArtistRoute(
    viewModel: ArtistViewModel,
    navigateToAlbum: NavFunction<TrackListArguments>,
    openAlbumContextMenu: NavFunction<AlbumContextMenuArguments>,
    navigateBack: NoArgNavFunction,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    ArtistScreen(
        state = state,
        navigateToAlbum = navigateToAlbum,
        openAlbumContextMenu = openAlbumContextMenu,
        navigateBack = navigateBack
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    state: ArtistState,
    navigateToAlbum: NavFunction<TrackListArguments>,
    openAlbumContextMenu: NavFunction<AlbumContextMenuArguments>,
    navigateBack: NoArgNavFunction,
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
                            painter = painterResource(id = R.drawable.ic_back),
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistLayout(
    state: ArtistState,
    navigateToAlbum: NavFunction<TrackListArguments>,
    openAlbumContextMenu: NavFunction<AlbumContextMenuArguments>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = state.listItems) { item ->
            when (item) {
                is ArtistScreenItem.SectionHeaderItem -> {
                    ListItem(headlineContent = {
                        Text(
                            text = stringResource(id = item.sectionType.sectionName),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    })
                }

                is ArtistScreenItem.AlbumRowItem -> {
                    ModelListItem(
                        state = item.state,
                        modifier = Modifier.clickable {
                            navigateToAlbum(TrackListArguments(trackList = MediaGroup.Album(albumId = item.id)))
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    openAlbumContextMenu(AlbumContextMenuArguments(albumId = item.id))
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
    }
}


