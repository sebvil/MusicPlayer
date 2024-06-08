package com.sebastianvm.musicplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.designsystem.components.ListItem
import com.sebastianvm.musicplayer.designsystem.components.Text

@Composable
fun ContextMenu(menuTitle: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        ListItem(headlineContent = { Text(text = menuTitle) })

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        content()
    }
}
