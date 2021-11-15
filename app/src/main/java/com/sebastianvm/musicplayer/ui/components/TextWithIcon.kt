package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
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
    val lineHeight = with(LocalDensity.current) {
        MaterialTheme.typography.headlineMedium.fontSize.toDp().plus(4.dp)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Icon(
            painter = painterResource(id = state.icon),
            contentDescription = state.iconContentDescription.getString(),
            modifier = Modifier.height(lineHeight),
        )
        Text(
            text = state.text.getString(),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
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
