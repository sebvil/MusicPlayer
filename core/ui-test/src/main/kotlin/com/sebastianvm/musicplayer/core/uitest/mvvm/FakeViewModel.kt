package com.sebastianvm.musicplayer.core.uitest.mvvm

import com.sebastianvm.musicplayer.core.ui.mvvm.NoState
import com.sebastianvm.musicplayer.core.ui.mvvm.NoUserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.EmptyCoroutineContext

class FakeStateHolder : BaseViewModel<NoState, NoUserAction>(viewModelScope = vmScope) {
    override val state: StateFlow<NoState> = MutableStateFlow(NoState).asStateFlow()

    override fun handle(action: NoUserAction) = Unit

    val viewModelScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
}
