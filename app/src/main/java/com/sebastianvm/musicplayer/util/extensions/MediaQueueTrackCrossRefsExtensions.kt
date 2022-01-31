package com.sebastianvm.musicplayer.util.extensions

import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef

fun List<MediaQueueTrackCrossRef>.withUpdatedIndices(): List<MediaQueueTrackCrossRef> =
    mapIndexed { index, mediaQueueTrackCrossRef ->
        mediaQueueTrackCrossRef.copy(trackIndex = index)
    }
