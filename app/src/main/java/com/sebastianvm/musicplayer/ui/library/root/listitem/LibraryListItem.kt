package com.sebastianvm.musicplayer.ui.library.root.listitem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val iconModifier = Modifier.size(40.dp)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LibraryListItem(item: LibraryItem, modifier: Modifier = Modifier, onItemClicked: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(id = item.rowName),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier.clickable { onItemClicked() },
        leadingContent = {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = stringResource(id = item.rowName),
                modifier = iconModifier,
            )
        },
        trailingContent = {
            Text(
                text = pluralStringResource(
                    id = item.countString,
                    count = item.count,
                    item.count
                ),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}