package com.sebastianvm.musicplayer.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.sebastianvm.commons.R
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

data class MediaArtImageState(
    val imageUri: String,
    @StringRes val contentDescription: Int,
    @DrawableRes val backupResource: Int,
    @StringRes val backupContentDescription: Int,
    val args: List<Any> = listOf()
)


@ComponentPreview
@Composable
fun MediaArtImagePreview() {
    ThemedPreview {
        MediaArtImage(
            uri = "",
            contentDescription = "",
            backupResource = R.drawable.ic_song,
            backupContentDescription = R.string.placeholder_album_art
        )
    }
}

@Composable
fun MediaArtImage(
    mediaArtImageState: MediaArtImageState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
) {
    MediaArtImage(
        uri = mediaArtImageState.imageUri,
        contentDescription = stringResource(
            id = mediaArtImageState.contentDescription,
            *mediaArtImageState.args.toTypedArray()
        ),
        backupResource = mediaArtImageState.backupResource,
        backupContentDescription = mediaArtImageState.backupContentDescription,
        modifier = modifier,
        backgroundColor = backgroundColor,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
    )
}

/**
 * Wrapper around the Image composable that takes in a DisplayableImage as the image input.
 */
@Composable
fun MediaArtImage(
    uri: String,
    contentDescription: String,
    @DrawableRes backupResource: Int,
    @StringRes backupContentDescription: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
) {
    val painter = rememberAsyncImagePainter(model = uri)

    Image(
        painter = painter,
        modifier = modifier,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
    )

    when (painter.state) {
        is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Error, is AsyncImagePainter.State.Empty -> {
            Icon(
                painter = painterResource(id = backupResource),
                contentDescription = stringResource(id = backupContentDescription),
                modifier = modifier.background(backgroundColor),
                tint = MaterialTheme.colorScheme.contentColorFor(backgroundColor),
            )
        }
        else -> Unit
    }

}
