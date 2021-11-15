package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sebastianvm.musicplayer.ui.theme.AppTheme
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme


@Composable
fun ThemedPreview(
    getBackgroundColor: @Composable () -> Color = { MaterialTheme.colorScheme.background },
    content: @Composable () -> Unit
) {
    AppTheme {
        M3AppTheme {
            Surface(
                color = getBackgroundColor()
            ) {
                content()
            }
        }
    }
}

