package com.sebastianvm.musicplayer.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.SearchMode
import com.sebastianvm.musicplayer.ui.components.chip.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface


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
fun SearchScreen(
    screenViewModel: SearchViewModel = viewModel(),
    navigationDelegate: NavigationDelegate
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) {
        SearchLayout(screenViewModel)
    }
}

@ScreenPreview
@Composable
fun SearchScreenPreview(@PreviewParameter(SearchStatePreviewParameterProvider::class) state: SearchState) {
    ScreenPreview {
        SearchLayout(DefaultViewModelInterfaceProvider.getDefaultInstance(state))
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchLayout(viewModel: ViewModelInterface<SearchState, SearchUserAction>) {
    val state by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val input = rememberSaveable {
        mutableStateOf("")
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
    }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        TextField(
            value = input.value,
            onValueChange = {
                input.value = it
                viewModel.handle(SearchUserAction.TextChanged(it))
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    style = LocalTextStyle.current,
                    color = LocalContentColor.current
                )
            },
            leadingIcon =
            {
                IconButton(onClick = {
                   viewModel.handle(SearchUserAction.UpButtonClicked)
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(
                            id = R.string.search
                        )
                    )
                }

            },
            trailingIcon = input.value.takeUnless { it.isEmpty() }?.let {
                {
                    IconButton(onClick = {
                        input.value = ""
                        viewModel.handle(SearchUserAction.TextChanged(it))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onAny = { keyboardController?.hide() },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        SingleSelectFilterChipGroup(
            options = SearchMode.values().toList(),
            selectedOption = state.selectedOption,
            modifier = Modifier.padding(vertical = AppDimensions.spacing.medium),
            getDisplayName = { stringResource(id = res) },
            onNewOptionSelected = { newOption ->
                focusManager.clearFocus()
                viewModel.handle(SearchUserAction.SearchModeChanged(newOption))
            }
        )
        LazyColumn {
            items(state.searchResults) { item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier
                        .clickable {
                            viewModel.handle(SearchUserAction.SearchResultClicked(item.id))
                        }
                        .clearFocusOnTouch(focusManager),
                    trailingContent = {
                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.handle(
                                    SearchUserAction.SearchResultOverflowMenuIconClicked(
                                        item.id
                                    )
                                )
                            },
                            modifier = Modifier.clearFocusOnTouch(focusManager)
                        ) {
                            Icon(
                                painter = painterResource(id = com.sebastianvm.commons.R.drawable.ic_overflow),
                                contentDescription = stringResource(id = com.sebastianvm.commons.R.string.more)
                            )
                        }
                    }
                )
            }
        }
    }
}
