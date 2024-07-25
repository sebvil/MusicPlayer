package com.sebastianvm.musicplayer.core.servicestest.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.mvvm.NoState
import com.sebastianvm.musicplayer.core.ui.mvvm.NoUserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.EmptyCoroutineContext

object NoStateHolder :
    com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder<
        com.sebastianvm.musicplayer.core.ui.mvvm.NoState,
        com.sebastianvm.musicplayer.core.ui.mvvm.NoUserAction> {
    override val state: StateFlow<com.sebastianvm.musicplayer.core.ui.mvvm.NoState> =
        MutableStateFlow(com.sebastianvm.musicplayer.core.ui.mvvm.NoState)

    override val stateHolderScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)

    override fun handle(action: com.sebastianvm.musicplayer.core.ui.mvvm.NoUserAction) = Unit
}

class FakeUiComponent<Args : Arguments>(val arguments: Args) : UiComponent<Args, NoStateHolder> {

    override val key: Any = ""

    override fun createStateHolder(services: Services): NoStateHolder = NoStateHolder

    @Composable override fun Content(modifier: Modifier) = Unit
}
