package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sebastianvm.musicplayer.MusicPlayerApplication
import com.sebastianvm.musicplayer.di.DependencyContainer
import kotlinx.coroutines.flow.StateFlow

interface StateHolder<S : State, A : UserAction> {
    val state: StateFlow<S>
    fun handle(action: A)
}

@Composable
fun <S : State, A : UserAction> stateHolder(
    factory: (dependencyContainer: DependencyContainer) -> StateHolder<S, A>
): StateHolder<S, A> {
    val dependencyContainer =
        (LocalContext.current.applicationContext as MusicPlayerApplication).dependencyContainer
    return remember {
        factory(dependencyContainer)
    }
}
