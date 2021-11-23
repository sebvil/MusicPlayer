package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


data class TextWithIconState(
    @DrawableRes val icon: Int,
    val text: DisplayableString,
    val iconContentDescription: DisplayableString
)

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TextWithIconPreview(@PreviewParameter(TextWithIconStateProvider::class) state: TextWithIconState) {
    ThemedPreview {
        TextWithIcon(state = state)
    }
}

@Composable
fun TextWithIcon(
    @PreviewParameter(TextWithIconStateProvider::class) state: TextWithIconState,
    modifier: Modifier = Modifier,
) {
    val fontStyle = MaterialTheme.typography.headlineMedium
    val iconInlineContentId = "iconInlineContent"
    val text = buildAnnotatedString {
        // Append a placeholder string "[myBox]" and attach an annotation "inlineContent" on it.
        appendInlineContent(iconInlineContentId, "[icon]")
        append(" ")
        append(state.text.getString())
    }
    val inlineContent = mapOf(
        Pair(
            // This tells the [BasicText] to replace the placeholder string "[myBox]" by
            // the composable given in the [InlineTextContent] object.
            iconInlineContentId,
            InlineTextContent(
                // Placeholder tells text layout the expected size and vertical alignment of
                // children composable.
                Placeholder(
                    width = fontStyle.fontSize,
                    height = fontStyle.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                val dimension = with(LocalDensity.current) {
                    fontStyle.fontSize.toDp()
                }
                Icon(
                    painter = painterResource(id = state.icon),
                    contentDescription = state.iconContentDescription.getString(),
                    modifier = Modifier.size(dimension)
                )
            }
        )
    )
    Text(
        text = text,
        style = fontStyle,
        modifier = modifier,
        inlineContent = inlineContent
    )
}

class TextWithIconStateProvider : PreviewParameterProvider<TextWithIconState> {
    override val values: Sequence<TextWithIconState>
        get() = sequenceOf(
            TextWithIconState(
                icon = R.drawable.ic_album,
                text = DisplayableString.ResourceValue(R.string.albums),
                iconContentDescription = DisplayableString.ResourceValue(R.string.albums)
            ),
            TextWithIconState(
                icon = R.drawable.ic_song,
                text = DisplayableString.ResourceValue(R.string.all_songs),
                iconContentDescription = DisplayableString.ResourceValue(R.string.song)
            ),
        )
}
