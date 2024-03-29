package com.sebastianvm.musicplayer.ui.bottomsheets.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegateImpl
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

@Suppress("ViewModelForwarding")
@RootNavGraph
@Destination(navArgsDelegate = SortMenuArguments::class, style = DestinationStyleBottomSheet::class)
@Composable
fun SortBottomSheet(
    navigator: DestinationsNavigator,
    sheetViewModel: SortBottomSheetViewModel = hiltViewModel()
) {
    Screen(
        screenViewModel = sheetViewModel,
        navigationDelegate = NavigationDelegateImpl(navigator)
    ) { state, screenDelegate ->
        SortBottomSheet(state = state, screenDelegate = screenDelegate)
    }
}

@Composable
fun SortBottomSheet(
    state: SortBottomSheetState,
    screenDelegate: ScreenDelegate<SortBottomSheetUserAction>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(WindowInsets.navigationBars.asPaddingValues())
    ) {
        ListItem(headlineContent = {
            Text(
                text = stringResource(id = R.string.sort_by),
                modifier = Modifier.paddingFromBaseline(top = 36.dp),
                style = MaterialTheme.typography.titleMedium
            )
        })
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        LazyColumn {
            items(state.sortOptions, key = { it }) { row ->
                ListItem(
                    modifier = Modifier
                        .clickable {
                            screenDelegate.handle(
                                SortBottomSheetUserAction.MediaSortOptionClicked(
                                    row
                                )
                            )
                        }
                        .let {
                            if (state.selectedSort == row) {
                                it.background(
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                            } else {
                                it
                            }
                        },
                    headlineContent = {
                        Text(
                            text = stringResource(id = row.stringId),
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingContent = {
                        if (state.selectedSort == row) {
                            Icon(
                                imageVector = when (state.sortOrder) {
                                    MediaSortOrder.ASCENDING -> Icons.Default.ArrowUpward
                                    MediaSortOrder.DESCENDING -> Icons.Default.ArrowDownward
                                },
                                contentDescription = if (state.sortOrder == MediaSortOrder.ASCENDING) {
                                    stringResource(
                                        R.string.up_arrow
                                    )
                                } else {
                                    stringResource(R.string.down_arrow)
                                },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }
    }
}
