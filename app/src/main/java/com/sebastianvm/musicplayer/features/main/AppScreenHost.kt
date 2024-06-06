package com.sebastianvm.musicplayer.features.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

@Composable
fun MainApp(state: MainState, handle: Handler<MainUserAction>, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                handle(MainUserAction.ConnectToMusicService)
            } else if (event == Lifecycle.Event.ON_STOP) {
                handle(MainUserAction.DisconnectFromMusicService)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val navBarPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    var height by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val playerBottomPadding = 4.dp

    val isFullScreen = state.isFullscreen

    BackHandler(enabled = isFullScreen) {
        if (isFullScreen) {
            handle(MainUserAction.CollapsePlayer)
        }
    }
    val transition = updateTransition(targetState = isFullScreen, label = "player animation")

    val paddingHorizontal by
        transition.animateDp(transitionSpec = transitionSpec(), label = "padding horizontal") {
            targetIsFullScreen ->
            if (targetIsFullScreen) 0.dp else 8.dp
        }

    val paddingBottom by
        transition.animateDp(transitionSpec = transitionSpec(), label = "padding bottom") {
            targetIsFullScreen ->
            if (targetIsFullScreen) 0.dp else (playerBottomPadding + navBarPadding)
        }
    val paddingValues by remember {
        derivedStateOf {
            with(density) { PaddingValues(bottom = paddingBottom + height.toDp() + 8.dp) }
        }
    }
    CompositionLocalProvider(LocalPaddingValues provides paddingValues) {
        Box(modifier = modifier) {
            state.appNavigationHostUiComponent.Content(modifier = Modifier)
            state.playerUiComponent.Content(
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = paddingBottom)
                        .padding(horizontal = paddingHorizontal)
                        .onPlaced { coordinates -> height = coordinates.boundsInParent().height }
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            enabled = !isFullScreen,
                        ) {
                            handle(MainUserAction.ExpandPlayer)
                        }
            )
        }
    }
}

private fun <T> transitionSpec():
    @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<T> = { spring() }
