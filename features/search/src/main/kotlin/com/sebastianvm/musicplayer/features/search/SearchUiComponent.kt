package com.sebastianvm.musicplayer.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.GenreRow
import com.sebastianvm.musicplayer.core.designsystems.components.Permission
import com.sebastianvm.musicplayer.core.designsystems.components.PermissionHandler
import com.sebastianvm.musicplayer.core.designsystems.components.PlaylistRow
import com.sebastianvm.musicplayer.core.designsystems.components.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.sync.LibrarySyncWorker
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import kotlinx.collections.immutable.toImmutableList

data class SearchUiComponent(val navController: NavController) :
    BaseUiComponent<SearchState, SearchUserAction, SearchStateHolder>() {

    override fun createStateHolder(services: Services): SearchStateHolder {
        return SearchStateHolder(
            ftsRepository = services.repositoryProvider.searchRepository,
            playbackManager = services.playbackManager,
            navController = navController,
            features = services.featureRegistry,
        )
    }

    @Composable
    override fun Content(
        state: SearchState,
        handle: Handler<SearchUserAction>,
        modifier: Modifier,
    ) {
        SearchScreen(state = state, handle = handle, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    handle: Handler<SearchUserAction>,
    modifier: Modifier = Modifier,
) {
    var isSearchActive by remember { mutableStateOf(false) }

    var query by remember {
        handle(SearchUserAction.TextChanged(""))
        mutableStateOf("")
    }

    var isDropdownManuExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val onActiveChange: (Boolean) -> Unit = {
        isSearchActive = it
        if (!it) {
            query = ""
            handle(SearchUserAction.TextChanged(""))
        }
    }
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    query = it
                    handle(SearchUserAction.TextChanged(it))
                },
                onSearch = {},
                expanded = isSearchActive,
                onExpandedChange = onActiveChange,
                placeholder = { Text(text = stringResource(RString.search_media)) },
                leadingIcon = {
                    if (isSearchActive) {
                        IconButton(
                            onClick = {
                                isSearchActive = false
                                query = ""
                                handle(SearchUserAction.TextChanged(""))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(id = RString.back),
                            )
                        }
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "")
                    }
                },
                trailingIcon = {
                    if (!isSearchActive) {
                        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                            IconButton(
                                onClick = { isDropdownManuExpanded = !isDropdownManuExpanded }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(id = RString.more),
                                )
                            }
                            DropdownMenu(
                                expanded = isDropdownManuExpanded,
                                onDismissRequest = { isDropdownManuExpanded = false },
                            ) {
                                PermissionHandler(
                                    permission = Permission.ReadAudio,
                                    dialogTitle = RString.storage_permission_needed,
                                    message = RString.grant_storage_permissions,
                                    onGrantPermission = {
                                        val syncRequest =
                                            OneTimeWorkRequestBuilder<LibrarySyncWorker>().build()
                                        WorkManager.getInstance(context).enqueue(syncRequest)
                                        isDropdownManuExpanded = false
                                    },
                                ) { onClick ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(stringResource(id = RString.refresh_library))
                                        },
                                        onClick = { onClick() },
                                        leadingIcon = {
                                            Icon(Icons.Outlined.Refresh, contentDescription = null)
                                        },
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
                                contentDescription = stringResource(id = RString.back),
                            )
                        }
                    }
                },
            )
        },
        expanded = isSearchActive,
        onExpandedChange = onActiveChange,
        modifier = modifier,
    ) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            SearchLayout(state = state, handle = handle)
        }
    }
}

@Composable
fun SearchLayout(
    state: SearchState,
    handle: Handler<SearchUserAction>,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxHeight().clearFocusOnTouch(focusManager)) {
        SingleSelectFilterChipGroup(
            options =
                com.sebastianvm.musicplayer.core.data.fts.SearchMode.entries.toImmutableList(),
            selectedOption = state.selectedOption,
            modifier = Modifier.padding(vertical = 16.dp),
            getDisplayName = { option -> stringResource(id = option.res) },
            onSelectNewOption = { newOption ->
                handle(SearchUserAction.SearchModeChanged(newOption))
            },
        )
        LazyColumn(contentPadding = LocalPaddingValues.current) {
            items(state.searchResults) { item ->
                val itemModifier =
                    Modifier.clickable { handle(SearchUserAction.SearchResultClicked(item)) }
                when (item) {
                    is SearchResult.Album -> {
                        AlbumRow(state = item.state, modifier = itemModifier)
                    }
                    is SearchResult.Artist -> {
                        ArtistRow(state = item.state, modifier = itemModifier)
                    }
                    is SearchResult.Genre -> {
                        GenreRow(state = item.state, modifier = itemModifier)
                    }
                    is SearchResult.Playlist -> {
                        PlaylistRow(state = item.state, modifier = itemModifier)
                    }
                    is SearchResult.Track -> {
                        TrackRow(state = item.state, modifier = itemModifier)
                    }
                }
            }
        }
    }
}

private fun Modifier.clearFocusOnTouch(focusManager: FocusManager): Modifier =
    this.pointerInput(key1 = null) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            focusManager.clearFocus()
        }
    }
