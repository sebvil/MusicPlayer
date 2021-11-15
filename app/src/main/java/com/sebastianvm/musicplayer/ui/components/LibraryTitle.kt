package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LibraryTitlePreview(@PreviewParameter(DisplayableStringProvider::class) title: DisplayableString) {
    ThemedPreview {
        LibraryTitle(title = title)
    }
}

/**
 * This is just a wrapper for a Text that is reused in multiple places, thus it is okay
 * to not pass in a modifier.
 */
@Composable
fun LibraryTitle(title: DisplayableString) {
    Text(
        text = title.getString(),
        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(
            start = AppDimensions.spacing.large,
            top = AppDimensions.spacing.large,
            bottom = AppDimensions.spacing.mediumLarge
        )
    )
}


class DisplayableStringProvider : PreviewParameterProvider<DisplayableString> {
    override val values: Sequence<DisplayableString>
        get() = sequenceOf(
            DisplayableString.ResourceValue(R.string.library),
            DisplayableString.ResourceValue(R.string.artists),
            DisplayableString.ResourceValue(R.string.all_songs),
            DisplayableString.ResourceValue(R.string.albums),
            DisplayableString.ResourceValue(R.string.genres),
            DisplayableString.StringValue("Tropipop")

        )
}


