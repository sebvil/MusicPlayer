package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.junit.Assert

suspend inline fun <reified F : UiEvent> BaseViewModel<*, *>.expectUiEvent(
    externalScope: CoroutineScope,
    crossinline checks: F.() -> Unit = {}
) {
    externalScope.launch {
        val event = eventsFlow.first()
        Assert.assertTrue(event is F)
        checks(event as F)
    }
    delay(1)
}
