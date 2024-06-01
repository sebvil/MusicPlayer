package com.sebastianvm.musicplayer.features.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil

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
