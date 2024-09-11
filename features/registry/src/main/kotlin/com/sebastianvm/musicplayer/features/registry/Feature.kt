package com.sebastianvm.musicplayer.features.registry

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.NoProps
import com.sebastianvm.musicplayer.core.ui.mvvm.Props
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Feature<A : Arguments, P : Props> {
    interface Key

    val initializer: MvvmComponent.Initializer<A, P>
}

fun <A : Arguments, P : Props> Feature<A, P>.create(
    arguments: A,
    props: StateFlow<P>,
): MvvmComponent<*, *, *> {
    return initializer.initialize(arguments, props)
}

fun <A : Arguments> Feature<A, NoProps>.create(arguments: A): MvvmComponent<*, *, *> {
    return initializer.initialize(arguments, MutableStateFlow(NoProps))
}
