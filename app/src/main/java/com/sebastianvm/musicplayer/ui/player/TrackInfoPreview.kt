package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

class TrackInfoStatePreviewParameterProvider : PreviewParameterProvider<TrackInfoState> {
    override val values = sequenceOf(
        TrackInfoState(
            trackName = PreviewUtil.randomString(minLength = 40),
            artists = PreviewUtil.randomString(minLength = 40)
        ),
        TrackInfoState(
            trackName = PreviewUtil.randomString(maxLength = 15),
            artists = PreviewUtil.randomString(maxLength = 15)
        ),
        TrackInfoState(
            trackName = PreviewUtil.randomString(maxLength = 15),
            artists = PreviewUtil.randomString(minLength = 40)
        ),
        TrackInfoState(
            trackName = PreviewUtil.randomString(minLength = 40),
            artists = PreviewUtil.randomString(maxLength = 15)
        )
    )
}

@ComponentPreview
@Composable
fun TrackInfoPreview(@PreviewParameter(TrackInfoStatePreviewParameterProvider::class) state: TrackInfoState) {
    ThemedPreview {
        Column {
            TrackInfo(state = state)
        }
    }
}