package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder

@Suppress("StateHolderForwarding")
@RootNavGraph
@Destination(
    navArgsDelegate = ArtistsMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun ArtistsBottomSheet(
    navigator: DestinationsNavigator,
    arguments: ArtistsMenuArguments,
    modifier: Modifier = Modifier,
    stateHolder: StateHolder<UiState<ArtistsBottomSheetState>, ArtistsBottomSheetUserAction> =
        stateHolder { dependencyContainer ->
            ArtistsBottomSheetStateHolder(
                arguments = arguments,
                artistRepository = dependencyContainer.repositoryProvider.artistRepository
            )
        }
) {
    val uiState by stateHolder.currentState

    UiStateScreen(
        uiState = uiState,
        emptyScreen = {},
        modifier = modifier
    ) { state ->
        ArtistsBottomSheetLayout(
            state = state,
            navigator = navigator,
        )
    }
}

@Composable
fun ArtistsBottomSheetLayout(
    state: ArtistsBottomSheetState,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.artists),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            modifier = Modifier.padding(top = 12.dp)
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        ModelList(
            state = state.modelListState,
            onBackButtonClicked = {},
            onItemClicked = { _, item ->
                navigator.navigate(
                    ArtistRouteDestination(
                        ArtistArguments(artistId = item.id)
                    )
                )
            }
        )
    }
}
