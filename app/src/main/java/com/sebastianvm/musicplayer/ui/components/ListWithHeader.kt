package com.sebastianvm.musicplayer.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.commons.util.ListItem

data class ListWithHeaderState<H, I : ListItem>(
    val header: H,
    val listItems: List<I>,
    val headerRenderer: @Composable (H) -> Unit,
    val rowRenderer: @Composable (I) -> Unit
)

@Composable
fun <H, I : ListItem> ListWithHeader(
    state: ListWithHeaderState<H, I>,
    modifier: Modifier = Modifier
) {
    with(state) {
        LazyColumn(modifier = modifier) {
            item {
                headerRenderer(header)
            }
            items(listItems, key = { i -> i.id }) { item ->
                rowRenderer(item)
            }
        }
    }
}