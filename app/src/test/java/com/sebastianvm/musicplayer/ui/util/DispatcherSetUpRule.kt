package com.sebastianvm.musicplayer.ui.util

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DispatcherSetUpRule : TestRule {
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                val mainThreadSurrogate = newSingleThreadContext("UI thread")
                Dispatchers.setMain(mainThreadSurrogate)
                try {
                    base?.evaluate()
                } finally {
                    Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
                    mainThreadSurrogate.close()
                }
            }
        }
    }

}