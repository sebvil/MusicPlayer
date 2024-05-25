package com.sebastianvm.musicplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ContextMenu(menuTitle: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(
                    text = menuTitle,
                    style = MaterialTheme.typography.titleMedium
                )
            },
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        content()
    }
}
