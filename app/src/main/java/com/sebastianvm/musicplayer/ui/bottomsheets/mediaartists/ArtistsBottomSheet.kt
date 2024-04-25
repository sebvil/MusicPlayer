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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegateImpl
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate
import com.sebastianvm.musicplayer.ui.util.mvvm.viewModel

@Suppress("ViewModelForwarding")
@RootNavGraph
@Destination(
    navArgsDelegate = ArtistsMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun ArtistsBottomSheet(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    sheetViewModel: ArtistsBottomSheetViewModel = viewModel()
) {
    Screen(
        screenViewModel = sheetViewModel,
        navigationDelegate = NavigationDelegateImpl(navigator),
        modifier = modifier
    ) { state, screenDelegate ->
        ArtistsBottomSheetLayout(state = state, screenDelegate = screenDelegate)
    }
}

@Composable
fun ArtistsBottomSheetLayout(
    state: ArtistsBottomSheetState,
    screenDelegate: ScreenDelegate<ArtistsBottomSheetUserAction>,
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
                screenDelegate.handle(
                    ArtistsBottomSheetUserAction.ArtistRowClicked(
                        item.id
                    )
                )
            }
        )
    }
}
