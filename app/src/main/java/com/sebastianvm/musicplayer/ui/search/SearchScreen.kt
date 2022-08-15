package com.sebastianvm.musicplayer.ui.search

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.components.ArtistRow
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.chip.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun SearchScreen(
    screenViewModel: SearchViewModel = viewModel(),
    navigationDelegate: NavigationDelegate
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) { state ->
        SearchLayout(state = state, delegate = object : SearchScreenDelegate {
            override fun onTextChanged(newText: String) {
                Log.i("SEARCH", "New text: $newText")
                screenViewModel.onTextChanged(newText = newText)
            }

            override fun onOptionChosen(@StringRes newOption: Int) {
                screenViewModel.onSearchTypeChanged(newType = newOption)
            }

            override fun onTrackClicked(trackId: Long) {
                screenViewModel.onTrackRowClicked(trackId)
            }

            override fun onTrackOverflowMenuClicked(trackId: Long) {
                screenViewModel.onTrackOverflowMenuClicked(trackId)
            }

            override fun onArtistClicked(artistId: Long) {
                screenViewModel.onArtistRowClicked(artistId)
            }

            override fun onArtistOverflowMenuClicked(artistId: Long) {
                screenViewModel.onArtistOverflowMenuClicked(artistId)
            }

            override fun onAlbumClicked(albumId: Long) {
                screenViewModel.onAlbumRowClicked(albumId)
            }

            override fun onAlbumOverflowMenuClicked(albumId: Long) {
                screenViewModel.onAlbumOverflowMenuClicked(albumId)
            }

            override fun onGenreClicked(genreId: Long) {
                screenViewModel.onGenreRowClicked(genreId)
            }

            override fun onGenreOverflowMenuClicked(genreId: Long) {
                screenViewModel.onGenreOverflowMenuClicked(genreId)
            }
        })
    }
}

@ScreenPreview
@Composable
fun SearchScreenPreview(@PreviewParameter(SearchStatePreviewParameterProvider::class) state: SearchState) {
    ScreenPreview {
        SearchLayout(state = state)
    }
}

interface SearchScreenDelegate {
    fun onTextChanged(newText: String) = Unit
    fun onOptionChosen(@StringRes newOption: Int) = Unit
    fun onTrackClicked(trackId: Long) = Unit
    fun onTrackOverflowMenuClicked(trackId: Long) = Unit
    fun onArtistClicked(artistId: Long) = Unit
    fun onArtistOverflowMenuClicked(artistId: Long) = Unit
    fun onAlbumClicked(albumId: Long) = Unit
    fun onAlbumOverflowMenuClicked(albumId: Long) = Unit
    fun onGenreClicked(genreId: Long) = Unit
    fun onGenreOverflowMenuClicked(genreId: Long) = Unit
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchLayout(
    state: SearchState,
    delegate: SearchScreenDelegate = object : SearchScreenDelegate {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onAny = { keyboardController?.hide() },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        SingleSelectFilterChipGroup(
            options = listOf(R.string.songs, R.string.artists, R.string.albums, R.string.genres),
            selectedOption = state.selectedOption,
            modifier = Modifier.padding(vertical = AppDimensions.spacing.medium),
            getDisplayName = { ResUtil.getString(context, this) },
            onNewOptionSelected = { newOption ->
                focusManager.clearFocus()
                delegate.onOptionChosen(newOption)
            }
        )
        when (state.selectedOption) {
            R.string.songs -> {
                state.trackSearchResults.collectAsLazyPagingItems().also { lazyPagingItems ->
                    LazyColumn {
                        items(lazyPagingItems) { item ->
                            item?.also {
                                TrackRow(
                                    state = it,
                                    modifier = Modifier
                                        .clickable {
                                            delegate.onTrackClicked(item.trackId)
                                        }
                                        .pointerInput(key1 = null) {
                                            forEachGesture {
                                                awaitPointerEventScope {
                                                    awaitFirstDown(requireUnconsumed = true)
                                                    focusManager.clearFocus()
                                                }

                                            }
                                        }) {
                                    delegate.onTrackOverflowMenuClicked(item.trackId)
                                }
                            }
                        }
                    }
                }
            }
            R.string.artists -> {
                state.artistSearchResults.collectAsLazyPagingItems().also { lazyPagingItems ->
                    LazyColumn {
                        items(lazyPagingItems) { item ->
                            item?.also {
                                ArtistRow(
                                    state = item,
                                    modifier = Modifier
                                        .clickable {
                                            delegate.onArtistClicked(
                                                item.artistId
                                            )
                                        }
                                        .pointerInput(key1 = null) {
                                            forEachGesture {
                                                awaitPointerEventScope {
                                                    awaitFirstDown(requireUnconsumed = true)
                                                    focusManager.clearFocus()
                                                }

                                            }
                                        }
                                ) {
                                    delegate.onArtistOverflowMenuClicked(item.artistId)
                                }
                            }
                        }
                    }
                }
            }
            R.string.albums -> {
                state.albumSearchResults.collectAsLazyPagingItems().also { lazyPagingItems ->
                    LazyColumn {
                        items(lazyPagingItems) { item ->
                            item?.also {
                                AlbumRow(
                                    state = it,
                                    modifier = Modifier
                                        .clickable { delegate.onAlbumClicked(it.albumId) }
                                        .pointerInput(key1 = null) {
                                            forEachGesture {
                                                awaitPointerEventScope {
                                                    awaitFirstDown(requireUnconsumed = true)
                                                    focusManager.clearFocus()
                                                }

                                            }
                                        }) {
                                    delegate.onAlbumOverflowMenuClicked(it.albumId)
                                }
                            }
                        }
                    }
                }
            }
            R.string.genres -> {
                state.genreSearchResults.collectAsLazyPagingItems().also { lazyPagingItems ->
                    LazyColumn {
                        items(lazyPagingItems) { item ->
                            item?.also { genre ->
                                SingleLineListItem(
                                    modifier = Modifier
                                        .clickable {
                                            delegate.onGenreClicked(
                                                genre.id
                                            )
                                        }
                                        .pointerInput(key1 = null) {
                                            forEachGesture {
                                                awaitPointerEventScope {
                                                    awaitFirstDown(requireUnconsumed = true)
                                                    focusManager.clearFocus()
                                                }

                                            }
                                        },
                                    afterListContent = {
                                        IconButton(
                                            onClick = {
                                                delegate.onGenreOverflowMenuClicked(
                                                    genre.id
                                                )
                                            },
                                            modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_overflow),
                                                contentDescription = stringResource(R.string.more)
                                            )
                                        }
                                    }
                                ) {
                                    Text(
                                        text = genre.genreName,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
