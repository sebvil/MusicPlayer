package com.sebastianvm.musicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.sebastianvm.musicplayer.designsystem.icons.AppIcons
import com.sebastianvm.musicplayer.designsystem.icons.painter

@Composable
fun MediaArtImage(
    artworkUri: String,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
) {
    val backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
    var actualBackgroundColor by remember { mutableStateOf(backgroundColor) }

    var useColorFilter by remember { mutableStateOf(true) }
    val colorScheme = MaterialTheme.colorScheme

    val colorFilter by
        remember(useColorFilter, colorScheme, actualBackgroundColor) {
            derivedStateOf {
                if (useColorFilter) {
                    ColorFilter.tint(colorScheme.contentColorFor(actualBackgroundColor))
                } else {
                    null
                }
            }
        }

    AsyncImage(
        model = artworkUri,
        placeholder = AppIcons.Album.painter(),
        error = AppIcons.Album.painter(),
        contentDescription = null,
        contentScale = contentScale,
        alignment = alignment,
        alpha = alpha,
        modifier = modifier.background(actualBackgroundColor),
        onSuccess = {
            actualBackgroundColor = Color.Transparent
            useColorFilter = false
        },
        colorFilter = colorFilter,
    )
}
