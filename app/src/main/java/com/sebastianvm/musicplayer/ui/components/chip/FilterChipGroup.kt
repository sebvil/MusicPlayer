package com.sebastianvm.musicplayer.ui.components.chip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

@Composable
fun <T> SingleSelectFilterChipGroup(
    options: List<T>,
    selectedOption: T?,
    modifier: Modifier = Modifier,
    getDisplayName: T.() -> String,
    onNewOptionSelected: (T) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = AppDimensions.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.small),
        modifier = modifier
    ) {
        items(options) { option ->
            FilterChip(
                selected = selectedOption == option,
                text = option.getDisplayName(),
                modifier = Modifier.clickable { onNewOptionSelected(option) })
        }
    }
}
