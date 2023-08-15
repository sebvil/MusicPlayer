package com.sebastianvm.musicplayer.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil.compose.AsyncImage
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreviews
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

private val drawableResources =
    sequenceOf(R.drawable.ic_song, R.drawable.ic_album, R.drawable.ic_artist, R.drawable.ic_genre)

class MediaArtImageStatePreviewParamsProvider : PreviewParameterProvider<MediaArtImageState> {
    override val values: Sequence<MediaArtImageState>
        get() = drawableResources.map {
            MediaArtImageState(
                imageUri = "",
                contentDescription = R.string.album_art_for_album,
                backupContentDescription = R.string.placeholder_album_art,
                backupResource = it,
                args = listOf("album")
            )
        }
}

data class MediaArtImageState(
    val imageUri: String,
    @StringRes val contentDescription: Int,
    @DrawableRes val backupResource: Int,
    @StringRes val backupContentDescription: Int,
    val args: List<Any> = listOf()
)

@ComponentPreviews
@Composable
private fun MediaArtImagePreview(
    @PreviewParameter(MediaArtImageStatePreviewParamsProvider::class) mediaArtImageState: MediaArtImageState
) {
    ThemedPreview {
        MediaArtImage(mediaArtImageState = mediaArtImageState)
    }
}

@Composable
fun MediaArtImage(
    mediaArtImageState: MediaArtImageState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) {
    MediaArtImage(
        uri = mediaArtImageState.imageUri,
        contentDescription = stringResource(
            id = mediaArtImageState.contentDescription,
            formatArgs = mediaArtImageState.args.toTypedArray()
        ),
        backupImage = painterResource(id = mediaArtImageState.backupResource),
        backupContentDescription = stringResource(id = mediaArtImageState.backupContentDescription),
        modifier = modifier,
        backgroundColor = backgroundColor,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha
    )
}

/**
 * Wrapper around the Image composable that takes in a DisplayableImage as the image input.
 */
@Composable
fun MediaArtImage(
    uri: String,
    contentDescription: String,
    backupImage: Painter,
    backupContentDescription: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) {
    var actualContentDescription by remember {
        mutableStateOf(backupContentDescription)
    }

    var actualBackgroundColor by remember {
        mutableStateOf(backgroundColor)
    }

    var useColorFilter by remember {
        mutableStateOf(true)
    }

    AsyncImage(
        model = uri,
        placeholder = backupImage,
        error = backupImage,
        contentDescription = actualContentDescription,
        contentScale = contentScale,
        alignment = alignment,
        alpha = alpha,
        modifier = modifier.background(actualBackgroundColor),
        onSuccess = {
            actualBackgroundColor = Color.Transparent
            useColorFilter = false
            actualContentDescription = contentDescription
        },
        colorFilter = if (useColorFilter) {
            ColorFilter.tint(
                MaterialTheme.colorScheme.contentColorFor(
                    actualBackgroundColor
                )
            )
        } else {
            null
        }
    )
}
