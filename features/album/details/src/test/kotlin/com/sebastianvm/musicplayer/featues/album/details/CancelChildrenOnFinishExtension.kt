package com.sebastianvm.musicplayer.featues.album.details

import io.kotest.core.listeners.AfterInvocationListener
import io.kotest.core.test.TestCase
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.cancelChildren

object CancelChildrenOnFinishExtension : AfterInvocationListener {

    override suspend fun afterInvocation(testCase: TestCase, iteration: Int) {
        coroutineContext.cancelChildren()
        super.afterInvocation(testCase, iteration)
    }
}
