package com.sebastianvm.musicplayer.ui.util

import androidx.annotation.CallSuper
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
open class BaseViewModelTest {

    @OptIn(DelicateCoroutinesApi::class)
    protected val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @CallSuper
    @Before
    open fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @CallSuper
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }


    suspend inline fun <reified F : UiEvent> BaseViewModel<*,*,*>.expectedUiEvent(
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
}