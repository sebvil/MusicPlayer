package com.sebastianvm.musicplayer.features.search

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.flowWithLifecycle
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.components.BottomSheet
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenu
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenu
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenu
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.repository.fts.SearchMode
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.Permission
import com.sebastianvm.musicplayer.ui.components.PermissionHandler
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.chip.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

fun Modifier.clearFocusOnTouch(focusManager: FocusManager): Modifier =
    this.pointerInput(key1 = null) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            focusManager.clearFocus()
        }
    }

@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    screenStateHolder: StateHolder<UiState<SearchState>, SearchUserAction> = stateHolder { dependencyContainer ->
        SearchStateHolder(
            ftsRepository = dependencyContainer.repositoryProvider.searchRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            albumContextMenuStateHolderFactory = AlbumContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            ),
            artistContextMenuStateHolderFactory = ArtistContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            ),
            genreContextMenuStateHolderFactory = GenreContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            ),
            playlistContextMenuStateHolderFactory = PlaylistContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            ),
            trackContextMenuStateHolderFactory = TrackContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            )
        )
    },
) {
    val uiState by screenStateHolder.currentState
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(uiState, lifecycle) {
        // If the date of birth is valid and the validation is in progress,
        // navigate to the next screen when `lifecycle` is at least STARTED,
        // which is the default Lifecycle.State for the `flowWithLifecycle` operator.
        snapshotFlow { uiState }
            .filterIsInstance<Data<SearchState>>()
            .map { it.state.navigationState }
            .flowWithLifecycle(lifecycle)
            .collect { destination ->
                if (destination != null) {
                    screenStateHolder.handle(SearchUserAction.NavigationCompleted)
                    navigator.navigate(destination)
                }
            }
    }

    UiStateScreen(
        uiState = uiState,
        modifier = modifier,
        emptyScreen = {
            Text(text = stringResource(R.string.no_results))
        }
    ) { state ->
        SearchScreen(
            state = state,
            handle = screenStateHolder::handle,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    handle: Handler<SearchUserAction>,
    modifier: Modifier = Modifier,
) {
    var isSearchActive by remember {
        mutableStateOf(false)
    }

    var query by remember {
        handle(SearchUserAction.TextChanged(""))
        mutableStateOf("")
    }

    var isDropdownManuExpanded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    SearchBar(
        query = query,
        onQueryChange = {
            query = it
            handle(SearchUserAction.TextChanged(it))
        },
        onSearch = {},
        modifier = modifier,
        active = isSearchActive,
        onActiveChange = {
            isSearchActive = it
            if (!it) {
                query = ""
                handle(SearchUserAction.TextChanged(""))
            }
        },
        leadingIcon = {
            if (isSearchActive) {
                IconButton(onClick = {
                    isSearchActive = false
                    query = ""
                    handle(SearchUserAction.TextChanged(""))
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            } else {
                Icon(imageVector = Icons.Default.Search, contentDescription = "")
            }
        },
        trailingIcon = {
            if (!isSearchActive) {
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    IconButton(
                        onClick = {
                            isDropdownManuExpanded = !isDropdownManuExpanded
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.more)
                        )
                    }
                    DropdownMenu(
                        expanded = isDropdownManuExpanded,
                        onDismissRequest = { isDropdownManuExpanded = false }
                    ) {
                        PermissionHandler(
                            permission = Permission.ReadAudio,
                            dialogTitle = R.string.storage_permission_needed,
                            message = R.string.grant_storage_permissions,
                            onPermissionGranted = {
                                startForegroundService(
                                    context,
                                    Intent(context, LibraryScanService::class.java)
                                )
                                isDropdownManuExpanded = false
                            }
                        ) { onClick ->
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.refresh_library)) },
                                onClick = {
                                    onClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Refresh,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            } else if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        query = ""
                        handle(SearchUserAction.TextChanged(""))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        },
        placeholder = {
            Text(text = stringResource(R.string.search_media))
        }
    ) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            SearchLayout(state = state, handle = handle)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLayout(
    state: SearchState,
    handle: Handler<SearchUserAction>,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                handle(SearchUserAction.DismissPlaybackErrorDialog)
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clearFocusOnTouch(focusManager)
    ) {
        SingleSelectFilterChipGroup(
            options = SearchMode.entries.toImmutableList(),
            selectedOption = state.selectedOption,
            modifier = Modifier.padding(vertical = 16.dp),
            getDisplayName = { stringResource(id = res) },
            onNewOptionSelected = { newOption ->
                handle(SearchUserAction.SearchModeChanged(newOption))
            }
        )
        LazyColumn(contentPadding = LocalPaddingValues.current) {
            items(state.searchResults) { item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier
                        .clickable {
                            handle(
                                SearchUserAction.SearchResultClicked(
                                    id = item.id,
                                    mediaType = state.selectedOption
                                )
                            )
                        },
                    onMoreClicked = {
                        handle(
                            SearchUserAction.SearchResultOverflowMenuIconClicked(
                                id = item.id,
                                mediaType = state.selectedOption
                            )
                        )
                    }
                )
            }
        }
    }

    state.albumContextMenuStateHolder?.let { albumContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(SearchUserAction.AlbumContextMenuDismissed)
            },
        ) {
            AlbumContextMenu(
                stateHolder = albumContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }

    state.artistContextMenuStateHolder?.let { artistContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(SearchUserAction.ArtistContextMenuDismissed)
            },
        ) {
            ArtistContextMenu(
                stateHolder = artistContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }

    state.genreContextMenuStateHolder?.let { genreContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(SearchUserAction.GenreContextMenuDismissed)
            },
        ) {
            GenreContextMenu(
                stateHolder = genreContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }

    state.trackContextMenuStateHolder?.let { trackContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(SearchUserAction.GenreContextMenuDismissed)
            },
        ) {
            TrackContextMenu(
                stateHolder = trackContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }

    state.playlistContextMenuStateHolder?.let { playlistContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(SearchUserAction.PlaylistContextMenuDismissed)
            },
        ) {
            PlaylistContextMenu(
                stateHolder = playlistContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}
