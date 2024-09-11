package com.sebastianvm.musicplayer.features.artist.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.annotations.MvvmComponent
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.OverflowIconButton
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.core.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState

@MvvmComponent(vmClass = ArtistListViewModel::class)
@Composable
fun ArtistList(
    state: UiState<ArtistListState>,
    handle: Handler<ArtistListUserAction>,
    modifier: Modifier = Modifier,
) {
    UiStateScreen(
        uiState = state,
        modifier = modifier.fillMaxSize(),
        emptyScreen = {
            StoragePermissionNeededEmptyScreen(
                message = RString.no_artists_found,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            )
        },
    ) { dataState ->
        ArtistList(state = dataState, handle = handle, modifier = Modifier)
    }
}

@Composable
fun ArtistList(
    state: ArtistListState,
    handle: Handler<ArtistListUserAction>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
        item {
            SortButton(
                state = state.sortButtonState,
                onClick = { handle(ArtistListUserAction.SortByButtonClicked) },
                modifier = Modifier.padding(start = 16.dp),
            )
        }
        items(state.artists, key = { item -> item.id }) { item ->
            ArtistRow(
                state = item,
                modifier =
                    Modifier.clickable { handle(ArtistListUserAction.ArtistClicked(item.id)) },
                trailingContent = {
                    OverflowIconButton(
                        onClick = { handle(ArtistListUserAction.ArtistMoreIconClicked(item.id)) }
                    )
                },
            )
        }
    }
}
