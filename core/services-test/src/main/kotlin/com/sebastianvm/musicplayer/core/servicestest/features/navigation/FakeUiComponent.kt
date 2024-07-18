package com.sebastianvm.musicplayer.core.servicestest.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.services.features.mvvm.Arguments
import com.sebastianvm.musicplayer.services.features.mvvm.NoState
import com.sebastianvm.musicplayer.services.features.mvvm.NoUserAction
import com.sebastianvm.musicplayer.services.features.mvvm.StateHolder
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NoStateHolder : StateHolder<NoState, NoUserAction> {
    override val state: StateFlow<NoState> = MutableStateFlow(NoState)

    override val stateHolderScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)

    override fun handle(action: NoUserAction) = Unit
}

class FakeUiComponent<Args : Arguments>(override val arguments: Args) :
    UiComponent<Args, NoStateHolder> {

    override val key: Any = ""

    override fun createStateHolder(
        services: com.sebastianvm.musicplayer.services.Services
    ): NoStateHolder = NoStateHolder

    @Composable override fun Content(modifier: Modifier) = Unit
}
