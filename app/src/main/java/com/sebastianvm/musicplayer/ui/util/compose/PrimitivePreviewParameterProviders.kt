package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class BooleanPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}