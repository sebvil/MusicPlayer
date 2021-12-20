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
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.DisplayableStringPreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LibraryTitlePreview(@PreviewParameter(DisplayableStringPreviewParameterProvider::class) title: DisplayableString) {
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
            start = AppDimensions.spacing.mediumLarge,
            top = AppDimensions.spacing.mediumLarge,
            bottom = AppDimensions.spacing.medium
        )
    )
}


