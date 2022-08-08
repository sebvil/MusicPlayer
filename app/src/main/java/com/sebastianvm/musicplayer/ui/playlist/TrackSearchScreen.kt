package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


@Composable
fun TrackSearchScreen(
    screenViewModel: TrackSearchViewModel = viewModel(),
    navigationDelegate: NavigationDelegate
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) { state ->
        TrackSearchLayout(state = state, delegate = object : TrackSearchScreenDelegate {
            override fun onTextChanged(newText: String) {
                screenViewModel.onTextChanged(newText = newText)
            }

            override fun onTrackClicked(trackId: Long) {
                screenViewModel.onTrackClicked(trackId = trackId)
            }

        })
    }
}

@ScreenPreview
@Composable
fun TrackSearchScreenPreview(@PreviewParameter(TrackSearchStatePreviewParameterProvider::class) state: TrackSearchState) {
    ScreenPreview {
        TrackSearchLayout(state = state)
    }
}

interface TrackSearchScreenDelegate {
    fun onTextChanged(newText: String) = Unit
    fun onTrackClicked(trackId: Long) = Unit
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSearchLayout(
    state: TrackSearchState,
    delegate: TrackSearchScreenDelegate = object : TrackSearchScreenDelegate {},
) {
    val input = rememberSaveable {
        mutableStateOf("")
    }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .focusRequester(focusRequester)
            .focusable(enabled = true, interactionSource)
            .clickable { focusRequester.requestFocus() }) {
        TextField(
            value = input.value,
            onValueChange = {
                input.value = it
                delegate.onTextChanged(it)
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    style = LocalTextStyle.current,
                    color = LocalContentColor.current
                )
            },
            leadingIcon = input.value.takeIf { it.isEmpty() }?.let {
                {
                    Icon(
                        imageVector = Icons.Default.Search,
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
                        delegate.onTextChanged("")
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
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth()
        )

        state.trackSearchResults.collectAsLazyPagingItems().also { lazyPagingItems ->
            LazyColumn {
                items(lazyPagingItems) { item ->
                    item?.also {
                        TrackRow(
                            state = it,
                            modifier = Modifier.clickable { delegate.onTrackClicked(it.trackId) },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_plus),
                                    contentDescription = stringResource(R.string.more),
                                )
                            })
                    }
                }
            }
        }
    }
}


