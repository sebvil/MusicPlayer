package com.sebastianvm.musicplayer.ui.components.lists

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

data class SortButtonState(@StringRes val text: Int, val sortOrder: MediaSortOrder)

data class ModelListState(
    val items: List<ModelListItemState>,
    val sortButtonState: SortButtonState?
)

@Composable
fun ModelList(
    state: ModelListState,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onSortButtonClicked: (() -> Unit)? = null,
    onItemClicked: (Int, ModelListItemState) -> Unit = { _, _ -> },
    onItemMoreIconClicked: (Int, ModelListItemState) -> Unit = { _, _ -> }
) {
    LazyColumn(modifier = modifier, state = listState) {
        state.sortButtonState?.let {
            item {
                TextButton(
                    onClick = { onSortButtonClicked?.invoke() })
                {
                    Icon(
                        imageVector = if (it.sortOrder == MediaSortOrder.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null
                    )
                    Text(text = stringResource(id = it.text))
                }
            }
        }
        itemsIndexed(state.items, key = { _, item -> item.id }) { index, item ->
            ModelListItem(
                state = item,
                modifier = Modifier
                    .clickable {
                        onItemClicked(index, item)
                    },
                onMoreClicked = {
                    onItemMoreIconClicked(index, item)
                }
            )
        }
    }
}