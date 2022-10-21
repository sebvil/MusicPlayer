package com.sebastianvm.musicplayer.ui.components.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SingleSelectFilterChipGroup(
    options: List<T>,
    selectedOption: T?,
    modifier: Modifier = Modifier,
    getDisplayName: @Composable T.() -> String,
    onNewOptionSelected: (T) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = AppDimensions.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.small),
        modifier = modifier.selectableGroup()
    ) {
        items(options) { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = { onNewOptionSelected(option) },
                label = { Text(text = option.getDisplayName()) },
            )
        }
    }
}
