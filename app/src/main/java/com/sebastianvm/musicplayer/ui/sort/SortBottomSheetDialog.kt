package com.sebastianvm.musicplayer.ui.sort

import androidx.annotation.StringRes
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
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import com.sebastianvm.musicplayer.util.SortOrder
import kotlinx.coroutines.Dispatchers


interface SortBottomSheetDelegate {
    fun popBackStack(@StringRes sortOption: Int)
}

@Composable
fun SortBottomSheet(
    sheetViewModel: SortBottomSheetViewModel = viewModel(),
    delegate: SortBottomSheetDelegate
) {
    val state = sheetViewModel.state.collectAsState(context = Dispatchers.Main)
    HandleEvents(eventsFlow = sheetViewModel.eventsFlow) { event ->
        when (event) {
            is SortBottomSheetUiEvent.CloseBottomSheet -> {
                delegate.popBackStack(event.sortOption)
            }
        }
    }

    val rowModifier = Modifier
        .fillMaxWidth()
        .height(AppDimensions.bottomSheet.rowHeight)
        .padding(start = AppDimensions.bottomSheet.startPadding)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = rowModifier) {
            Text(
                text = stringResource(id = R.string.sort_by),
                modifier = Modifier.paddingFromBaseline(top = 36.dp)
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        LazyColumn {
            items(state.value.sortOptions, key = { it }) { row ->
                Row(
                    modifier = Modifier
                        .clickable {
                            sheetViewModel.handle(
                                SortBottomSheetUserAction.SortOptionSelected(
                                    row
                                )
                            )
                        }.let {
                            if (state.value.selectedSort == row) {
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
                    if (state.value.selectedSort == row) {
                        Icon(
                            painter = painterResource(id = if (state.value.sortOrder == SortOrder.ASCENDING) R.drawable.ic_up else R.drawable.ic_down),
                            contentDescription = "temp",
                            modifier = Modifier.padding(end = AppDimensions.spacing.large)
                        )
                        Text(text = stringResource(id = row), modifier = Modifier.weight(1f))
                    } else {
                        Text(
                            text = stringResource(id = row),
                            modifier = Modifier.padding(start = 24.dp.plus(AppDimensions.spacing.large))
                        )
                    }

                }
            }
        }
    }
}

