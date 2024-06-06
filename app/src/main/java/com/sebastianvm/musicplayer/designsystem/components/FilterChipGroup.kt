package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T> SingleSelectFilterChipGroup(
    options: ImmutableList<T>,
    selectedOption: T?,
    getDisplayName: @Composable (T) -> String,
    onNewOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.selectableGroup(),
    ) {
        items(options) { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = { onNewOptionSelected(option) },
                label = { Text(text = getDisplayName(option)) },
            )
        }
    }
}
