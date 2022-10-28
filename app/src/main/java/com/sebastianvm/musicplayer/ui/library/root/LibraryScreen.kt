package com.sebastianvm.musicplayer.ui.library.root

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.ui.components.PermissionHandler
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryListItem
import com.sebastianvm.musicplayer.ui.library.root.searchbox.SearchBox
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import kotlin.math.roundToInt

@OptIn(ExperimentalLifecycleComposeApi::class)
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
    var isButtonVisible by remember {
        mutableStateOf(true)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = consumed.y.roundToInt()
                if (delta == 0) {
                    return Offset.Zero
                }
                isButtonVisible = delta > 0

                return Offset.Zero
            }
        }
    }

    val context = LocalContext.current
    Scaffold(
        modifier = modifier.nestedScroll(nestedScrollConnection),
        floatingActionButton = {
            AnimatedVisibility(
                visible = isButtonVisible,
                enter = scaleIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                PermissionHandler(
                    permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE,
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
            modifier = Modifier.padding(paddingValues)
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
                    start = AppDimensions.spacing.mediumLarge,
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
