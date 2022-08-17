package com.sebastianvm.musicplayer.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {

    private var error: Throwable? = null

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()
    protected val testScope = TestScope(dispatcherSetUpRule.dispatcher)


    fun TestScope.runReliableTest(block: suspend TestScope.() -> Unit) = this.runTest {
        Thread.setDefaultUncaughtExceptionHandler { _, tr -> error = tr }
        block()
        error?.also { throw it }
    }
}