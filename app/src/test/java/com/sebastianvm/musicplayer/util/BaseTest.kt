package com.sebastianvm.musicplayer.util

import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.extension.RegisterExtension

abstract class BaseTest {

    protected val testScope: TestScope
        get() = coroutineExtension.testScope

    protected val dispatcher: TestDispatcher = coroutineExtension.dispatcher

    companion object {
        @JvmStatic
        @RegisterExtension
        val coroutineExtension = CoroutineTestExtension()
    }
}
