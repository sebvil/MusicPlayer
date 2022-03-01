package com.sebastianvm.musicplayer.ui.search

import android.content.res.Configuration
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.components.ArtistRow
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.chip.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.theme.textFieldColors
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface SearchNavigationDelegate {
    fun navigateToPlayer()
    fun navigateToArtist(artistName: String)
    fun navigateToAlbum(albumId: String)
    fun navigateToGenre(genreName: String)
    fun openContextMenu(mediaType: MediaType, mediaGroup: MediaGroup)
}


@Composable
fun SearchScreen(
    screenViewModel: SearchViewModel = viewModel(),
    delegate: SearchNavigationDelegate
) {
    Screen(screenViewModel = screenViewModel, eventHandler = { event ->
        when (event) {
            is SearchUiEvent.NavigateToPlayer -> delegate.navigateToPlayer()
            is SearchUiEvent.NavigateToArtist -> delegate.navigateToArtist(event.artistName)
            is SearchUiEvent.NavigateToAlbum -> delegate.navigateToAlbum(event.albumId)
            is SearchUiEvent.NavigateToGenre -> delegate.navigateToGenre(event.genreName)
            is SearchUiEvent.OpenContextMenu -> delegate.openContextMenu(
                event.mediaType,
                event.mediaGroup,
            )
        }
    }) { state ->
        SearchLayout(state = state, delegate = object : SearchScreenDelegate {
            override fun onTextChanged(newText: String) {
                Log.i("SEARCH", "New text: $newText")
                screenViewModel.handle(SearchUserAction.OnTextChanged(newText = newText))
            }

            override fun onOptionChosen(@StringRes newOption: Int) {
                screenViewModel.handle(SearchUserAction.SearchTypeChanged(newType = newOption))
            }

            override fun onTrackClicked(trackId: String) {
                screenViewModel.handle(SearchUserAction.TrackRowClicked(trackId))
            }

            override fun onTrackOverflowMenuClicked(trackId: String) {
                screenViewModel.handle(SearchUserAction.TrackOverflowMenuClicked(trackId))
            }

            override fun onArtistClicked(artistName: String) {
                screenViewModel.handle(SearchUserAction.ArtistRowClicked(artistName))
            }

            override fun onArtistOverflowMenuClicked(artistName: String) {
                screenViewModel.handle(SearchUserAction.ArtistOverflowMenuClicked(artistName))
            }

            override fun onAlbumClicked(albumId: String) {
                screenViewModel.handle(SearchUserAction.AlbumRowClicked(albumId))
            }

            override fun onAlbumOverflowMenuClicked(albumId: String) {
                screenViewModel.handle(SearchUserAction.AlbumOverflowMenuClicked(albumId))
            }

            override fun onGenreClicked(genreName: String) {
                screenViewModel.handle(SearchUserAction.GenreRowClicked(genreName))
            }

            override fun onGenreOverflowMenuClicked(genreName: String) {
                screenViewModel.handle(SearchUserAction.GenreOverflowMenuClicked(genreName))
            }
        })
    }
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchScreenPreview(@PreviewParameter(SearchStatePreviewParameterProvider::class) state: SearchState) {
    ScreenPreview {
        SearchLayout(state = state)
    }
}

interface SearchScreenDelegate {
    fun onTextChanged(newText: String) = Unit
    fun onOptionChosen(@StringRes newOption: Int) = Unit
    fun onTrackClicked(trackId: String) = Unit
    fun onTrackOverflowMenuClicked(trackId: String) = Unit
    fun onArtistClicked(artistName: String) = Unit
    fun onArtistOverflowMenuClicked(artistName: String) = Unit
    fun onAlbumClicked(albumId: String) = Unit
    fun onAlbumOverflowMenuClicked(albumId: String) = Unit
    fun onGenreClicked(genreName: String) = Unit
    fun onGenreOverflowMenuClicked(genreName: String) = Unit
}

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
            textStyle = LocalTextStyle.current,
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
            colors = textFieldColors(),
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
                                TrackRow(
                                    state = it,
                                    modifier = Modifier.clickable { delegate.onTrackClicked(item.trackId) }) {
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
                                    modifier = Modifier.clickable {
                                        delegate.onArtistClicked(
                                            item.artistName
                                        )
                                    }) {
                                    delegate.onArtistOverflowMenuClicked(item.artistName)
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
                                    modifier = Modifier.clickable { delegate.onAlbumClicked(it.albumId) }) {
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
                                    modifier = Modifier.clickable {
                                        delegate.onGenreClicked(
                                            genre.genreName
                                        )
                                    },
                                    afterListContent = {
                                        IconButton(
                                            onClick = {
                                                delegate.onGenreOverflowMenuClicked(
                                                    genre.genreName
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
