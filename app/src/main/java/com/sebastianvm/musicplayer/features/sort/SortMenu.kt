package com.sebastianvm.musicplayer.features.sort

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.navigation.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

data class SortMenu(override val arguments: SortMenuArguments) : Screen<SortMenuArguments> {
    @Composable
    override fun Content(modifier: Modifier) {
        SortMenu(
            stateHolder = rememberSortMenuStateHolder(arguments = arguments),
            modifier = modifier
        )
    }
}

@Composable
fun SortMenu(stateHolder: SortMenuStateHolder, modifier: Modifier = Modifier) {
    val state by stateHolder.currentState
    SortMenu(
        state = state,
        handle = stateHolder::handle,
        modifier = modifier
    )
}

@Composable
fun SortMenu(
    state: SortMenuState,
    handle: Handler<SortMenuUserAction>,
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
                val clickableModifier = state.selectedSort?.let {
                    Modifier.clickable {
                        handle(
                            SortMenuUserAction.MediaSortOptionClicked(
                                newSortOption = row,
                                selectedSort = state.selectedSort,
                                currentSortOrder = state.sortOrder
                            )
                        )
                    }
                } ?: Modifier

                val backgroundModifier = if (state.selectedSort == row) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                } else {
                    Modifier
                }
                ListItem(
                    modifier = Modifier
                        .then(clickableModifier)
                        .then(backgroundModifier),
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
