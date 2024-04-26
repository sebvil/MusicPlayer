package com.sebastianvm.musicplayer

import io.kotest.core.listeners.AfterInvocationListener
import io.kotest.core.test.TestCase
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.coroutineContext

object CancelChildrenOnFinishExtension : AfterInvocationListener {

    override suspend fun afterInvocation(testCase: TestCase, iteration: Int) {
        coroutineContext.cancelChildren()
        super.afterInvocation(testCase, iteration)
    }
}
