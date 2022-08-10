package com.sebastianvm.musicplayer.ui.components

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


@ComponentPreview
@Composable
fun MediaArtImagePreview() {
    ThemedPreview {
        MediaArtImage(
            uri = Uri.EMPTY,
            contentDescription = "",
            backupResource = R.drawable.ic_song,
            backupContentDescription = R.string.placeholder_album_art
        )
    }
}

/**
 * Wrapper around the Image composable that takes in a DisplayableImage as the image input.
 */
@Composable
fun MediaArtImage(
    uri: Uri,
    contentDescription: String,
    @DrawableRes backupResource: Int,
    @StringRes backupContentDescription: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
) {
    val description = mutableStateOf(contentDescription)
    val context = LocalContext.current
    Surface(
        color = backgroundColor,
        modifier = modifier
    ) {
        AsyncImage(
            model = uri,
            contentDescription = description.value,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            placeholder = painterResource(id = backupResource),
            error = painterResource(id = backupResource),
            fallback = painterResource(id = backupResource),
            onError = {
                description.value = ResUtil.getString(context, backupContentDescription)
            }
        )
    }
}
