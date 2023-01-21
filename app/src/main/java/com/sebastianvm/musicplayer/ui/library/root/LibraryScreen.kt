package com.sebastianvm.musicplayer.ui.library.root

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.ui.components.Permission
import com.sebastianvm.musicplayer.ui.components.PermissionHandler
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryListItem
import com.sebastianvm.musicplayer.ui.library.root.searchbox.SearchBox
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

@Composable
fun LibraryRoute(
    viewModel: LibraryViewModel,
    navigateToSearchScreen: () -> Unit,
    navigateToAllTracksList: () -> Unit,
    navigateToArtistList: () -> Unit,
    navigateToAlbumList: () -> Unit,
    navigateToGenreList: () -> Unit,
    navigateToPlaylistList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    LibraryScreen(
        state = state,
        navigateToSearchScreen = navigateToSearchScreen,
        navigateToAllTracksList = navigateToAllTracksList,
        navigateToArtistList = navigateToArtistList,
        navigateToAlbumList = navigateToAlbumList,
        navigateToGenreList = navigateToGenreList,
        navigateToPlaylistList = navigateToPlaylistList,
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LibraryScreen(
    state: LibraryState,
    modifier: Modifier = Modifier,
    navigateToSearchScreen: () -> Unit,
    navigateToAllTracksList: () -> Unit,
    navigateToArtistList: () -> Unit,
    navigateToAlbumList: () -> Unit,
    navigateToGenreList: () -> Unit,
    navigateToPlaylistList: () -> Unit,
) {

    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {},
        floatingActionButton = {
            PermissionHandler(
                permission = Permission.ReadAudio,
                dialogTitle = R.string.storage_permission_needed,
                message = R.string.grant_storage_permissions,
                onPermissionGranted = {
                    startForegroundService(
                        context,
                        Intent(context, LibraryScanService::class.java)
                    )
                }
            ) { onClick ->
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = stringResource(id = R.string.scan),
                        )
                    },
                    onClick = onClick,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_scan),
                            contentDescription = stringResource(id = R.string.scan)
                        )
                    },
                )
            }
        }) { paddingValues ->
        LibraryLayout(
            state = state,
            onSearchBoxClicked = navigateToSearchScreen,
            onTracksItemClicked = navigateToAllTracksList,
            onArtistsItemClicked = navigateToArtistList,
            onAlbumsItemClicked = navigateToAlbumList,
            onGenresItemClicked = navigateToGenreList,
            onPlaylistsItemClicked = navigateToPlaylistList,
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
        )
    }
}


@Composable
fun LibraryLayout(
    state: LibraryState,
    onSearchBoxClicked: () -> Unit,
    onTracksItemClicked: () -> Unit,
    onArtistsItemClicked: () -> Unit,
    onAlbumsItemClicked: () -> Unit,
    onGenresItemClicked: () -> Unit,
    onPlaylistsItemClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO Use keys + content type
    LazyColumn(modifier = modifier) {
        item {
            SearchBox(modifier = Modifier
                .padding(all = AppDimensions.spacing.medium)
                .clickable { onSearchBoxClicked() })
        }
        item {
            Text(
                text = stringResource(id = R.string.library),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(
                    start = AppDimensions.spacing.medium,
                    bottom = AppDimensions.spacing.medium
                )
            )
        }
        item { LibraryListItem(item = state.tracksItem, onItemClicked = onTracksItemClicked) }
        item { LibraryListItem(item = state.artistsItem, onItemClicked = onArtistsItemClicked) }
        item { LibraryListItem(item = state.albumsItem, onItemClicked = onAlbumsItemClicked) }
        item { LibraryListItem(item = state.genresItem, onItemClicked = onGenresItemClicked) }
        item { LibraryListItem(item = state.playlistsItem, onItemClicked = onPlaylistsItemClicked) }
    }
}
