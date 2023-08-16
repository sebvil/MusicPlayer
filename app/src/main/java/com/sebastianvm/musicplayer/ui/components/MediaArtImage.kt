package com.sebastianvm.musicplayer.ui.components

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil.compose.AsyncImage
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.icons.Album
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreviews
import com.sebastianvm.musicplayer.ui.util.compose.IconState
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.ui.util.compose.painter

private val drawableResources =
    sequenceOf(Icons.Album)

class MediaArtImageStatePreviewParamsProvider : PreviewParameterProvider<MediaArtImageState> {
    override val values: Sequence<MediaArtImageState>
        get() = drawableResources.map {
            MediaArtImageState(
                imageUri = "",
                contentDescription = R.string.album_art_for_album,
                backupContentDescription = R.string.placeholder_album_art,
                backupImage = it,
                args = listOf("album")
            )
        }
}

data class MediaArtImageState(
    val imageUri: String,
    @StringRes val contentDescription: Int,
    val backupImage: IconState,
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
        backupImage = mediaArtImageState.backupImage,
        backupContentDescription = stringResource(id = mediaArtImageState.backupContentDescription),
        modifier = modifier,
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
    backupImage: IconState,
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
    val colorScheme = MaterialTheme.colorScheme

    val colorFilter by remember(useColorFilter, colorScheme, actualBackgroundColor) {
        derivedStateOf {
            if (useColorFilter) {
                ColorFilter.tint(
                    colorScheme.contentColorFor(
                        actualBackgroundColor
                    )
                )
            } else {
                null
            }
        }
    }

    AsyncImage(
        model = uri,
        placeholder = backupImage.painter(),
        error = backupImage.painter(),
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
        colorFilter = colorFilter,
    )
}
