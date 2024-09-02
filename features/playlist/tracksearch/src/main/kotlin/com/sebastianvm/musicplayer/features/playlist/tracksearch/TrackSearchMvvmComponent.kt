package com.sebastianvm.musicplayer.features.playlist.tracksearch

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseMvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments

data class TrackSearchMvvmComponent(
    val arguments: TrackSearchArguments,
    val navController: NavController,
    val playlistRepository: PlaylistRepository,
    val searchRepository: FullTextSearchRepository,
) : BaseMvvmComponent<TrackSearchState, TrackSearchUserAction, TrackSearchViewModel>() {

    override val viewModel: TrackSearchViewModel by lazy {
        TrackSearchViewModel(
            arguments = arguments,
            playlistRepository = playlistRepository,
            ftsRepository = searchRepository,
            navController = navController,
        )
    }

    @Composable
    override fun Content(
        state: TrackSearchState,
        handle: Handler<TrackSearchUserAction>,
        modifier: Modifier,
    ) {
        TrackSearch(state = state, handle = handle, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TrackSearch(
    state: TrackSearchState,
    handle: Handler<TrackSearchUserAction>,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    val context = LocalContext.current
    LaunchedEffect(key1 = state.trackAddedToPlaylist) {
        if (state.trackAddedToPlaylist != null) {
            Toast.makeText(
                    context,
                    context.getString(RString.track_added_to_playlist, state.trackAddedToPlaylist),
                    Toast.LENGTH_SHORT,
                )
                .show()
            handle(TrackSearchUserAction.ToastShown)
        }
    }

    var query by remember { mutableStateOf("") }
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    query = it
                    handle(TrackSearchUserAction.TextChanged(it))
                },
                onSearch = {},
                expanded = true,
                placeholder = { Text(text = stringResource(RString.search_tracks)) },
                leadingIcon = {
                    IconButton(onClick = { handle(TrackSearchUserAction.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = RString.back),
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                query = ""
                                handle(TrackSearchUserAction.TextChanged(""))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = RString.back),
                            )
                        }
                    }
                },
                onExpandedChange = {
                    if (!it) {
                        handle(TrackSearchUserAction.BackClicked)
                    }
                },
            )
        },
        colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
        expanded = true,
        onExpandedChange = {
            if (!it) {
                handle(TrackSearchUserAction.BackClicked)
            }
        },
        modifier = modifier.focusRequester(focusRequester),
    ) {
        LazyColumn(
            contentPadding =
                if (WindowInsets.isImeVisible) {
                    WindowInsets.ime.asPaddingValues()
                } else {
                    LocalPaddingValues.current
                }
        ) {
            items(state.trackSearchResults, key = { it.state.id to it.inPlaylist }) { item ->
                TrackRow(
                    state = item.state,
                    modifier =
                        Modifier.clickable(enabled = !item.inPlaylist) {
                                handle(
                                    TrackSearchUserAction.TrackClicked(
                                        trackId = item.state.id,
                                        trackName = item.state.trackName,
                                    )
                                )
                            }
                            .animateItem(),
                    trailingContent = {
                        if (item.inPlaylist) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    },
                )
            }
        }
    }
}
