package com.sebastianvm.musicplayer.core.uitest.mvvm

import com.sebastianvm.musicplayer.core.ui.mvvm.NoState
import com.sebastianvm.musicplayer.core.ui.mvvm.NoUserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeStateHolder : StateHolder<NoState, NoUserAction> {
    override val state: StateFlow<NoState> = MutableStateFlow(NoState).asStateFlow()

    override fun handle(action: NoUserAction) = Unit

    override val stateHolderScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
}
