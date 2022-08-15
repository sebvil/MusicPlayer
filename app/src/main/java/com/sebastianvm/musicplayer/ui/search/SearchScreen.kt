package com.sebastianvm.musicplayer.ui.search

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.chip.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLayout(
    state: SearchState,
    delegate: SearchScreenDelegate = object : SearchScreenDelegate {},
) {
    val context = LocalContext.current
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
        SingleSelectFilterChipGroup(
            options = listOf(R.string.songs, R.string.artists, R.string.albums, R.string.genres),
            selectedOption = state.selectedOption,
            modifier = Modifier.padding(vertical = AppDimensions.spacing.medium),
            getDisplayName = { ResUtil.getString(context, this) },
            onNewOptionSelected = { newOption -> delegate.onOptionChosen(newOption) }
        )
        when (state.selectedOption) {
            R.string.songs -> {
                state.trackSearchResults.collectAsLazyPagingItems().also { lazyPagingItems ->
                    LazyColumn {
                        items(lazyPagingItems) { item ->
                            item?.also {
                                TrackRow(state = item, modifier = Modifier
                                    .clickable {
                                        delegate.onTrackClicked(it.id)
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                delegate.onTrackOverflowMenuClicked(item.trackId)
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_overflow),
                                                contentDescription = stringResource(R.string.more),
                                            )
                                        }
                                    })
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
                                ModelListItem(
                                    state = item,
                                    modifier = Modifier.clickable {
                                        delegate.onArtistClicked(it.id)
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                delegate.onArtistOverflowMenuClicked(it.id)
                                            },
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
            }
            R.string.albums -> {
                state.albumSearchResults.collectAsLazyPagingItems().also { lazyPagingItems ->
                    LazyColumn {
                        items(lazyPagingItems) { item ->
                            item?.also {
                                ModelListItem(
                                    state = item,
                                    modifier = Modifier.clickable {
                                        delegate.onAlbumClicked(it.id)
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                delegate.onAlbumOverflowMenuClicked(it.id)
                                            },
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
            }
            R.string.genres -> {
                state.genreSearchResults.collectAsLazyPagingItems().also { lazyPagingItems ->
                    LazyColumn {
                        items(lazyPagingItems) { item ->
                            item?.also { genre ->
                                ModelListItem(
                                    state = item,
                                    modifier = Modifier.clickable {
                                        delegate.onGenreClicked(genre.id)
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                delegate.onGenreOverflowMenuClicked(genre.id)
                                            },
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
            }
        }
    }
}
