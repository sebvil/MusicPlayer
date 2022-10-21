package com.sebastianvm.musicplayer.ui.bottomsheets.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

@Composable
fun SortBottomSheet(
    sheetViewModel: SortBottomSheetViewModel = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = sheetViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) { state, screenDelegate ->
        SortBottomSheet(state = state, screenDelegate = screenDelegate)
    }
}

@Composable
fun SortBottomSheet(
    state: SortBottomSheetState,
    screenDelegate: ScreenDelegate<SortBottomSheetUserAction>
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .height(AppDimensions.bottomSheet.rowHeight)
        .padding(start = AppDimensions.bottomSheet.startPadding)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = rowModifier) {
            Text(
                text = stringResource(id = R.string.sort_by),
                modifier = Modifier.paddingFromBaseline(top = 36.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        LazyColumn {
            items(state.sortOptions, key = { it }) { row ->
                Row(
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
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            } else {
                                it
                            }
                        }
                        .then(rowModifier),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.selectedSort == row) {
                        Icon(
                            painter = painterResource(id = if (state.sortOrder == MediaSortOrder.ASCENDING) R.drawable.ic_up else R.drawable.ic_down),
                            contentDescription = if (state.sortOrder == MediaSortOrder.ASCENDING) stringResource(
                                R.string.up_arrow
                            )
                            else stringResource(R.string.down_arrow),
                            modifier = Modifier.padding(end = AppDimensions.spacing.mediumLarge),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(id = row.stringId),
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = stringResource(id = row.stringId),
                            modifier = Modifier.padding(start = 24.dp.plus(AppDimensions.spacing.mediumLarge))
                        )
                    }

                }
            }
        }
    }
}

