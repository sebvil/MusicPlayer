package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MediaArtImagePreview() {
    ThemedPreview {
        MediaArtImage(
            image = MediaArt(
                uris = listOf(),
                contentDescription = DisplayableString.StringValue(""),
                backupResource = R.drawable.ic_song,
                backupContentDescription = DisplayableString.StringValue("Album art placeholder")
            ),
            iconPadding = PaddingValues(all = 2.dp)
        )
    }
}

/**
 * Wrapper around the Image composable that takes in a DisplayableImage as the image input.
 */
@Composable
fun MediaArtImage(
    image: MediaArt,
    modifier: Modifier = Modifier,
    iconPadding: PaddingValues = PaddingValues(),
    backgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
) {
    androidx.compose.material3.Surface(
        color = backgroundColor,
        modifier = modifier
    ) {
        image.getBitmap()?.also {
            Image(
                bitmap = it,
                contentDescription = image.contentDescription.getString(),
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
            )
        } ?: run {
            Icon(
                painter = painterResource(id = image.backupResource),
                contentDescription = image.backupContentDescription.getString(),
                modifier = Modifier.padding(iconPadding),
                tint = LocalContentColor.current
            )

        }
    }

}