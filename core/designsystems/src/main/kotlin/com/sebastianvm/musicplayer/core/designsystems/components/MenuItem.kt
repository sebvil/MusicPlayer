package com.sebastianvm.musicplayer.core.designsystems.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MenuItem(
    text: String,
    icon: ImageVector,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = { Text(text = text) },
        modifier = modifier.clickable { onItemClick() },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        },
    )
}
