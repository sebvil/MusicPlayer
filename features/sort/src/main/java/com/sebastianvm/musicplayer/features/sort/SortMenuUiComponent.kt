package com.sebastianvm.musicplayer.features.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.core.designsystems.components.ListItem
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.designsystems.extensions.stringId
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments

data class SortMenuUiComponent(val arguments: SortMenuArguments) :
    BaseUiComponent<SortMenuState, SortMenuUserAction, SortMenuStateHolder>() {
    override fun createStateHolder(services: Services): SortMenuStateHolder {
        return getSortMenuStateHolder(services, arguments)
    }

    @Composable
    override fun Content(
        state: SortMenuState,
        handle: Handler<SortMenuUserAction>,
        modifier: Modifier,
    ) {
        SortMenu(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun SortMenu(
    state: SortMenuState,
    handle: Handler<SortMenuUserAction>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(text = stringResource(id = RString.sort_by)) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        )
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        LazyColumn {
            items(state.sortOptions, key = { it.name }) { row ->
                val clickableModifier =
                    state.selectedSort?.let {
                        Modifier.clickable {
                            handle(
                                SortMenuUserAction.MediaSortOptionClicked(
                                    newSortOption = row,
                                    selectedSort = state.selectedSort,
                                    currentSortOrder = state.sortOrder,
                                )
                            )
                        }
                    } ?: Modifier

                val backgroundModifier =
                    if (state.selectedSort == row) {
                        Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
                    } else {
                        Modifier
                    }
                ListItem(
                    modifier = Modifier.then(clickableModifier).then(backgroundModifier),
                    headlineContent = {
                        Text(
                            text = stringResource(id = row.stringId),
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    leadingContent = {
                        if (state.selectedSort == row) {
                            Icon(
                                imageVector =
                                    when (state.sortOrder) {
                                        MediaSortOrder.ASCENDING -> Icons.Default.ArrowUpward
                                        MediaSortOrder.DESCENDING -> Icons.Default.ArrowDownward
                                    },
                                contentDescription =
                                    if (state.sortOrder == MediaSortOrder.ASCENDING) {
                                        stringResource(RString.up_arrow)
                                    } else {
                                        stringResource(RString.down_arrow)
                                    },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}
