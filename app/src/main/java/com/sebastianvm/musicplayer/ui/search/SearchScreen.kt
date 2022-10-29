package com.sebastianvm.musicplayer.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.fts.SearchMode
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.chip.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.searchfield.SearchField
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate


fun Modifier.clearFocusOnTouch(focusManager: FocusManager): Modifier =
    this.pointerInput(key1 = null) {
        forEachGesture {
            awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = true)
                focusManager.clearFocus()
            }
        }
    }


@Composable
fun SearchScreen(screenViewModel: SearchViewModel, navigationDelegate: NavigationDelegate) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) { state, delegate ->
        SearchLayout(
            state = state,
            screenDelegate = delegate
        )
    }
}

@Composable
fun SearchLayout(state: SearchState, screenDelegate: ScreenDelegate<SearchUserAction>) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
    }

    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                screenDelegate.handle(SearchUserAction.DismissPlaybackErrorDialog)
            }
        })

    Column(modifier = Modifier.fillMaxHeight()) {
        SearchField(
            onTextChanged = { screenDelegate.handle(SearchUserAction.TextChanged(it)) },
            onUpButtonClicked = { screenDelegate.handle(SearchUserAction.UpButtonClicked) },
            focusRequester = focusRequester
        )
        SingleSelectFilterChipGroup(
            options = SearchMode.values().toList(),
            selectedOption = state.selectedOption,
            modifier = Modifier.padding(vertical = AppDimensions.spacing.medium),
            getDisplayName = { stringResource(id = res) },
            onNewOptionSelected = { newOption ->
                focusManager.clearFocus()
                screenDelegate.handle(SearchUserAction.SearchModeChanged(newOption))
            }
        )
        LazyColumn {
            items(state.searchResults) { item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier
                        .clickable {
                            screenDelegate.handle(SearchUserAction.SearchResultClicked(item.id))
                        }
                        .clearFocusOnTouch(focusManager),
                    trailingContent = {
                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                screenDelegate.handle(
                                    SearchUserAction.SearchResultOverflowMenuIconClicked(
                                        item.id
                                    )
                                )
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
