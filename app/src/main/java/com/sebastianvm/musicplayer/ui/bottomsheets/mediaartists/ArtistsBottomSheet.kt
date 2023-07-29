package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegateImpl
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@RootNavGraph
@Destination(
    navArgsDelegate = ArtistsMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun ArtistsBottomSheet(
    navigator: DestinationsNavigator,
    sheetViewModel: ArtistsBottomSheetViewModel = hiltViewModel(),
) {
    Screen(
        screenViewModel = sheetViewModel,
        navigationDelegate = NavigationDelegateImpl(navigator)
    ) { state, screenDelegate ->
        ArtistsBottomSheetLayout(state = state, screenDelegate = screenDelegate)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistsBottomSheetLayout(
    state: ArtistsBottomSheetState,
    screenDelegate: ScreenDelegate<ArtistsBottomSheetUserAction>
) {
    LazyColumn {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.bottomSheet.rowHeight)
                    .padding(start = AppDimensions.bottomSheet.startPadding),
            ) {
                Text(
                    text = stringResource(id = R.string.artists),
                    modifier = Modifier.paddingFromBaseline(top = 36.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
        items(state.artistList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    screenDelegate.handle(ArtistsBottomSheetUserAction.ArtistRowClicked(item.id))
                }
            )

        }
    }
}
