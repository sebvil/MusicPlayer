package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.ui.tooling.preview.PreviewParameterProvider


class StringPreviewParameterProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String>
        get() = sequence {
            for (i in 1..10) {
                yield(PreviewUtil.randomString())
            }
        }

}
