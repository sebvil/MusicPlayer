package com.sebastianvm.musicplayer.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {

    private val error: MutableStateFlow<Throwable?> = MutableStateFlow(null)

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()
    protected val testScope = TestScope(dispatcherSetUpRule.dispatcher)

    fun TestScope.runReliableTest(block: suspend TestScope.() -> Unit) = this.runTest {
        Thread.setDefaultUncaughtExceptionHandler { _, tr -> error.value = tr }
        val job = launch {
            error.collect { e ->
                e?.also { throw it }
            }
        }
        block()
        job.cancel()
    }
}
