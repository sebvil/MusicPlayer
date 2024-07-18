package com.sebastianvm.musicplayer.features.genre.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.track.list.TopBar

data class GenreDetailsUiComponent(
    override val arguments: GenreDetailsArguments,
    val navController: NavController,
) :
    BaseUiComponent<
        GenreDetailsArguments,
        GenreDetailsState,
        GenreDetailsUserAction,
        GenreDetailsStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): GenreDetailsStateHolder {
        return GenreDetailsStateHolder(
            args = arguments,
            navController = navController,
            genreRepository = dependencies.repositoryProvider.genreRepository,
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
            playbackManager = dependencies.playbackManager,
        )
    }

    @Composable
    override fun Content(
        state: GenreDetailsState,
        handle: Handler<GenreDetailsUserAction>,
        modifier: Modifier,
    ) {
        GenreDetails(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun GenreDetails(
    state: GenreDetailsState,
    handle: Handler<GenreDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                title = state.genreName,
                onBackButtonClick = { handle(GenreDetailsUserAction.BackClicked) },
            )
        },
    ) { paddingValues ->
        when (state) {
            is GenreDetailsState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            is GenreDetailsState.Data -> {
                GenreDetails(
                    state = state,
                    handle = handle,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
fun GenreDetails(
    state: GenreDetailsState.Data,
    handle: Handler<GenreDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    if (state.tracks.isEmpty()) {
        StoragePermissionNeededEmptyScreen(
            message = RString.no_tracks_found,
            modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        )
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = LocalPaddingValues.current,
            state = listState,
        ) {
            item {
                SortButton(
                    state = state.sortButtonState,
                    onClick = { handle(GenreDetailsUserAction.SortButtonClicked) },
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            itemsIndexed(state.tracks, key = { index, item -> index to item.id }) { index, item ->
                TrackRow(
                    state = item,
                    modifier =
                        Modifier.animateItem().clickable {
                            handle(GenreDetailsUserAction.TrackClicked(trackIndex = index))
                        },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                handle(
                                    GenreDetailsUserAction.TrackMoreIconClicked(
                                        trackId = item.id,
                                        trackPositionInList = index,
                                    ))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(id = RString.more),
                                )
                            }
                    },
                )
            }
        }
    }
}
