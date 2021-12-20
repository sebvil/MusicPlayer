package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.util.DisplayableString

class BooleanPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

class DisplayableStringPreviewParameterProvider : PreviewParameterProvider<DisplayableString> {
    override val values: Sequence<DisplayableString>
        get() = sequenceOf(
            DisplayableString.ResourceValue(com.sebastianvm.commons.R.string.library),
            DisplayableString.ResourceValue(com.sebastianvm.commons.R.string.artists),
            DisplayableString.ResourceValue(com.sebastianvm.commons.R.string.all_songs),
            DisplayableString.ResourceValue(com.sebastianvm.commons.R.string.albums),
            DisplayableString.ResourceValue(com.sebastianvm.commons.R.string.genres),
            DisplayableString.StringValue("Tropipop")
        )
}