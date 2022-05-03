package com.sebastianvm.musicplayer.ui.queue

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class QueueStatePreviewParameterProvider : PreviewParameterProvider<QueueState> {
    override val values = sequenceOf(
        QueueState(
            mediaGroup = null,
            queueItems = listOf(),
            nowPlayingTrackIndex = -1,
        )
    )
}