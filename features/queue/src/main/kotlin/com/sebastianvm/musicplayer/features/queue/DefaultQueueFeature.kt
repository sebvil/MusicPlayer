package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.queue.QueueFeature

class DefaultQueueFeature : QueueFeature {
    override fun queueUiComponent(): UiComponent<*> {
        return QueueUiComponent
    }
}
