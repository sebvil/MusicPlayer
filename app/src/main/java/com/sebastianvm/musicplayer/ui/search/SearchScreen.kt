package com.sebastianvm.musicplayer.ui.search

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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
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
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleNavEvents


fun Modifier.clearFocusOnTouch(focusManager: FocusManager): Modifier =
    this.pointerInput(key1 = null) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            focusManager.clearFocus()
        }
    }


@Composable
fun SearchScreen(screenViewModel: SearchViewModel, navigationDelegate: NavigationDelegate) {
    val uiState by screenViewModel.stateFlow.collectAsStateWithLifecycle()
    HandleNavEvents(viewModel = screenViewModel, navigationDelegate = navigationDelegate)
    UiStateScreen(uiState = uiState, emptyScreen = {
        Text(text = stringResource(R.string.no_results))
    }) { state ->
        SearchScreen(
            state
        ) {
            screenViewModel.handle(it)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(state: SearchState, screenDelegate: ScreenDelegate<SearchUserAction>) {
    var isSearchActive by remember {
        mutableStateOf(false)
    }

    var query by remember {
        screenDelegate.handle(SearchUserAction.TextChanged(""))
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
            screenDelegate.handle(SearchUserAction.TextChanged(it))
        },
        onSearch = {},
        active = isSearchActive,
        onActiveChange = {
            isSearchActive = it
            if (!it) {
                query = ""
                screenDelegate.handle(SearchUserAction.TextChanged(""))
            }
        },
        leadingIcon = {
            if (isSearchActive) {
                IconButton(onClick = {
                    isSearchActive = false
                    query = ""
                    screenDelegate.handle(SearchUserAction.TextChanged(""))
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
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
                        }) {
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
                                })
                        }
                    }

                }
            } else if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        query = ""
                        screenDelegate.handle(SearchUserAction.TextChanged(""))
                    }) {
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
            SearchLayout(state = state, screenDelegate = screenDelegate)
        }
    }
}

@Composable
fun SearchLayout(state: SearchState, screenDelegate: ScreenDelegate<SearchUserAction>) {
    val focusManager = LocalFocusManager.current

    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                screenDelegate.handle(SearchUserAction.DismissPlaybackErrorDialog)
            }
        })

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .clearFocusOnTouch(focusManager)
    ) {
        SingleSelectFilterChipGroup(
            options = SearchMode.values().toList(),
            selectedOption = state.selectedOption,
            modifier = Modifier.padding(vertical = AppDimensions.spacing.medium),
            getDisplayName = { stringResource(id = res) },
            onNewOptionSelected = { newOption ->
                screenDelegate.handle(SearchUserAction.SearchModeChanged(newOption))
            }
        )
        LazyColumn(contentPadding = LocalPaddingValues.current) {
            items(state.searchResults) { item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier
                        .clickable {
                            screenDelegate.handle(SearchUserAction.SearchResultClicked(item.id))
                        },
                    onMoreClicked = {
                        screenDelegate.handle(
                            SearchUserAction.SearchResultOverflowMenuIconClicked(
                                item.id
                            )
                        )
                    }
                )
            }
        }
    }
}
