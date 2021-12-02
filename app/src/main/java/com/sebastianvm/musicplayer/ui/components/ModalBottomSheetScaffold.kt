package com.sebastianvm.musicplayer.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : ListItem> ModalBottomSheetScaffold(
    @StringRes title: Int,
    items: List<T>,
    rowRenderer: @Composable (T, Modifier) -> Unit,
    onRowClicked: (rowGid: String) -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(sheetContent = {
        BottomSheetLayout(
            title = title,
            listItems = items,
            rowRenderer = rowRenderer,
            onRowClicked = onRowClicked
        )
    }) {
        content()
    }
}

@Composable
fun <T : ListItem> BottomSheetLayout(
    @StringRes title: Int,
    listItems: List<T>,
    rowRenderer: @Composable (T, Modifier) -> Unit,
    onRowClicked: (rowGid: String) -> Unit,
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(start = 16.dp)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = title),
            modifier = rowModifier.paddingFromBaseline(top = 36.dp)
        )
        LazyColumn {
            items(listItems, key = { i -> i.gid }) { row ->
                rowRenderer(
                    row,
                    Modifier
                        .clickable { onRowClicked(row.gid) }
                        .then(rowModifier)
                )
            }
        }
    }
}


@Preview
@Composable
fun BottomSheetLayoutPreview() {

    val getListItem = { name: String ->
        object : ListItem {
            override val gid: String
                get() = name
        }
    }
    var selectedItem by remember { mutableStateOf(getListItem("Track name")) }

    ThemedPreview {
        BottomSheetLayout(
            title = R.string.sort_by,
            listItems = listOf(
                getListItem("Track name"),
                getListItem("Artists names"),
                getListItem("Album name"),
                getListItem("Custom"),
            ),
            rowRenderer = { row, modifier ->
                Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedItem.gid == row.gid, onClick = { selectedItem = row })
                    Text(text = row.gid)
                }
            },
            onRowClicked = {
                selectedItem = getListItem(it)
            }
        )
    }
}

