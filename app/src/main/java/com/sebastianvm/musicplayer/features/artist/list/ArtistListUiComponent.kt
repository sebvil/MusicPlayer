package com.sebastianvm.musicplayer.features.artist.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.designsystem.components.ArtistRow
import com.sebastianvm.musicplayer.designsystem.components.OverflowIconButton
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.resources.RString

data class ArtistListUiComponent(val navController: NavController) :
    BaseUiComponent<
        NoArguments,
        UiState<ArtistListState>,
        ArtistListUserAction,
        ArtistListStateHolder,
    >() {
    override val arguments: NoArguments = NoArguments

    override fun createStateHolder(dependencies: Dependencies): ArtistListStateHolder {
        return getArtistListStateHolder(dependencies = dependencies, navController = navController)
    }

    @Composable
    override fun Content(
        state: UiState<ArtistListState>,
        handle: Handler<ArtistListUserAction>,
        modifier: Modifier,
    ) {
        ArtistList(uiState = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun ArtistList(
    uiState: UiState<ArtistListState>,
    handle: Handler<ArtistListUserAction>,
    modifier: Modifier = Modifier,
) {
    UiStateScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        emptyScreen = {
            StoragePermissionNeededEmptyScreen(
                message = RString.no_artists_found,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            )
        },
    ) { state ->
        ArtistList(state = state, handle = handle, modifier = Modifier)
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
