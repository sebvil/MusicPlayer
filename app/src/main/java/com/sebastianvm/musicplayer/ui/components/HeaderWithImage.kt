package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

data class HeaderWithImageState(
    val image: MediaArt,
    val title: DisplayableString
)


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HeaderWithImagePreview(
    @PreviewParameter(HeaderWithImageStatePreviewParameterProvider::class) state: HeaderWithImageState,
) {
    ThemedPreview {
        HeaderWithImage(state = state)
    }
}

@Composable
fun HeaderWithImage(
    @PreviewParameter(HeaderWithImageStatePreviewParameterProvider::class) state: HeaderWithImageState,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        MediaArtImage(
            image = state.image,
            modifier = Modifier.aspectRatio(1f).padding(32.dp),
            iconPadding = PaddingValues(all = 32.dp),
            contentScale = ContentScale.FillHeight
        )
        Text(
            text = state.title.getString(),
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

class HeaderWithImageStatePreviewParameterProvider :
    PreviewParameterProvider<HeaderWithImageState> {
    override val values = sequenceOf(
        HeaderWithImageState(
            image = MediaArt(
                uris = listOf(),
                contentDescription = DisplayableString.StringValue(""),
                backupResource = R.drawable.ic_song,
                backupContentDescription = DisplayableString.StringValue("Album art placeholder")
            ),
            title = DisplayableString.StringValue("10:20:40")
        )
    )
}