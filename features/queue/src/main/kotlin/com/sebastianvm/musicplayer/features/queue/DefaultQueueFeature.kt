package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.queue.QueueFeature

class DefaultQueueFeature : QueueFeature {
    override fun queueUiComponent(): MvvmComponent {
        return QueueMvvmComponent
    }
}
