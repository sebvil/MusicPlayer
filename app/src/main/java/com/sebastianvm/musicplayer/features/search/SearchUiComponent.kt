package com.sebastianvm.musicplayer.features.search

import android.content.Intent
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
import androidx.compose.material3.Text
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
import androidx.core.content.ContextCompat.startForegroundService
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.components.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.repository.fts.SearchMode
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.Permission
import com.sebastianvm.musicplayer.ui.components.PermissionHandler
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import kotlinx.collections.immutable.toImmutableList

data class SearchUiComponent(val navController: NavController) :
    BaseUiComponent<NoArguments, SearchState, SearchUserAction, SearchStateHolder>() {
    override val arguments: NoArguments = NoArguments

    override fun createStateHolder(dependencies: AppDependencies): SearchStateHolder {
        return getSearchStateHolder(dependencies, navController)
    }

    @Composable
    override fun Content(
        state: SearchState,
        handle: Handler<SearchUserAction>,
        modifier: Modifier
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
                placeholder = {
                    Text(text = stringResource(R.string.search_media))
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
            getDisplayName = { option -> stringResource(id = option.res) },
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
                    trailingContent = {
                        IconButton(onClick = {
                            handle(
                                SearchUserAction.SearchResultOverflowMenuIconClicked(
                                    id = item.id,
                                    mediaType = state.selectedOption
                                )
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(id = R.string.more)
                            )
                        }
                    }
                )
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
