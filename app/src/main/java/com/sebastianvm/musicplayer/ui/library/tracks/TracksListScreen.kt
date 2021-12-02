package com.sebastianvm.musicplayer.ui.library.tracks

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.M3ModalBottomSheetLayout
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import kotlinx.coroutines.launch


interface TracksListScreenNavigationDelegate {
    fun navigateToPlayer()
    fun navigateUp()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksListScreen(
    screenViewModel: TracksListViewModel = viewModel(),
    bottomNavBar: @Composable () -> Unit,
    delegate: TracksListScreenNavigationDelegate
) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is TracksListUiEvent.NavigateToPlayer -> {
                    delegate.navigateToPlayer()
                }
                is TracksListUiEvent.ShowBottomSheet -> {
                    scope.launch {
                        bottomSheetState.show()
                    }
                }
            }
        },
        topBar = { state ->
            TopBar(state = state, delegate = object : TopBarDelegate {
                override fun navigateUp() {
                    delegate.navigateUp()
                }

                override fun sortByClicked() {
                    screenViewModel.handle(TracksListUserAction.SortByClicked)
                }
            })
        },
        bottomNavBar = bottomNavBar,
        bottomSheet = { state, content ->
            TracksListBottomSheetMenu(
                sheetState = bottomSheetState,
                currentSort = state.currentSort,
                onSortSelected = { newSortOption ->
                    screenViewModel.handle(
                        TracksListUserAction.SortOptionClicked(newSortOption)
                    )
                }) {
                content()
            }
        }
    ) { state ->
        TracksListLayout(
            state = state,
            delegate = object : TracksListScreenDelegate {
                override fun onTrackClicked(trackGid: String) {
                    screenViewModel.handle(
                        TracksListUserAction.TrackClicked(
                            trackGid
                        )
                    )
                }
            })
    }
}


interface TopBarDelegate {
    fun navigateUp() = Unit
    fun sortByClicked() = Unit
}

@Composable
fun TopBar(state: TracksListState, delegate: TopBarDelegate) {
    SmallTopAppBar(
        title = {
            Text(text = state.tracksListTitle.getString())
        },
        navigationIcon = {
            IconButton(onClick = { delegate.navigateUp() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(onClick = { delegate.sortByClicked() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = stringResource(id = R.string.sort_by)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopBarPreview(@PreviewParameter(TracksListStatePreviewParameterProvider::class) state: TracksListState) {
    ThemedPreview {
        TopBar(state = state, delegate = object : TopBarDelegate {})
    }
}


interface TracksListScreenDelegate {
    fun onTrackClicked(trackGid: String)
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksListScreenPreview(@PreviewParameter(TracksListStatePreviewParameterProvider::class) state: TracksListState) {
    ScreenPreview(topBar = { TopBar(state = state, delegate = object : TopBarDelegate {}) }) {
        TracksListLayout(state = state, delegate = object : TracksListScreenDelegate {
            override fun onTrackClicked(trackGid: String) = Unit
        })
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksListLayout(
    state: TracksListState,
    delegate: TracksListScreenDelegate
) {

    LazyColumn {
        items(state.tracksList) { item ->
            TrackRow(
                state = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { delegate.onTrackClicked(item.trackGid) }
                    .padding(
                        vertical = AppDimensions.spacing.mediumSmall,
                        horizontal = AppDimensions.spacing.large
                    )
            )
        }

    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksListBottomSheetMenu(
    sheetState: ModalBottomSheetState,
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    content: @Composable () -> Unit
) {
    M3ModalBottomSheetLayout(
        sheetContent = { SortBottomSheetLayout(currentSort, onSortSelected) },
        sheetState = sheetState
    ) {
        content()
    }
}

@Composable
fun SortBottomSheetLayout(currentSort: SortOption, onSortSelected: (SortOption) -> Unit) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(start = 16.dp)
    val listItems = SortOption.values()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.sort_by),
            modifier = rowModifier.paddingFromBaseline(top = 36.dp)
        )
        LazyColumn {
            items(listItems, key = { it }) { row ->
                Row(
                    modifier = Modifier
                        .clickable { onSortSelected(row) }
                        .then(rowModifier),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSort == row,
                        onClick = { onSortSelected(row) })
                    Text(text = stringResource(id = row.id))
                }
            }
        }
    }

}