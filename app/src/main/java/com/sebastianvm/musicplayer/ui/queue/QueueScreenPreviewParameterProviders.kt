package com.sebastianvm.musicplayer.ui.queue

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class QueueStatePreviewParameterProvider : PreviewParameterProvider<QueueState> {
    override val values = sequenceOf(
        QueueState(
            queues = listOf(),
            mediaGroup = null,
            queueItems = listOf(),
            draggedItem = null,
            nowPlayingTrackId = "",
            dropdownExpanded = false
        )
    )
}