package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScreenPreview(screen: @Composable () -> Unit) {
    AppScreen { _, contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            screen()
        }
    }
}