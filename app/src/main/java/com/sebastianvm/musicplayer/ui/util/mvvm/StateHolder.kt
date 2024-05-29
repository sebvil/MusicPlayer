package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.MusicPlayerApplication
import com.sebastianvm.musicplayer.di.DependencyContainer
import kotlinx.coroutines.flow.StateFlow

interface StateHolder<S : State, A : UserAction> {
    val state: StateFlow<S>
    fun handle(action: A)
}

@Composable
fun <S : State, A : UserAction, SH : StateHolder<S, A>> rememberStateHolder(
    factory: (dependencies: DependencyContainer) -> SH
): SH {
    val dependencies =
        (LocalContext.current.applicationContext as MusicPlayerApplication).dependencies
    return remember {
        factory(dependencies)
    }
}

val <S : State, A : UserAction> StateHolder<S, A>.currentState: androidx.compose.runtime.State<S>
    @Composable
    get() = state.collectAsStateWithLifecycle()
