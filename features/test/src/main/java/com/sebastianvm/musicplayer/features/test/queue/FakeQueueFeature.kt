package com.sebastianvm.musicplayer.features.test.queue

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.queue.QueueFeature

class FakeQueueFeature : QueueFeature {
    override fun queueUiComponent(): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "Queue")
    }
}
