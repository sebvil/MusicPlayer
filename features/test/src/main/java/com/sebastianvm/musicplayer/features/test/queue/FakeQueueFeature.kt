package com.sebastianvm.musicplayer.features.test.queue

import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.queue.QueueFeature

class FakeQueueFeature : QueueFeature {
    override fun queueUiComponent(): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments = null, name = "Queue")
    }
}
