package com.sebastianvm.musicplayer.ui

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.features.navigation.AppNavigationHost
import com.sebastianvm.musicplayer.features.navigation.AppNavigationHostStateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

fun <T> transitionSpec(): @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<T> =
    { spring() }


@Composable
fun MainScreen(stateHolder: MainViewModel, modifier: Modifier = Modifier) {
    val state by stateHolder.currentState
    AppScreenHost(
        mainState = state, handle = stateHolder::handle
    ) {
        AppNavigationHost(stateHolder = remember {
            AppNavigationHostStateHolder()
        })
    }
}

@Composable
fun AppScreenHost(
    mainState: MainState,
    handle: Handler<MainUserAction>,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.systemBars,
    content: @Composable () -> Unit
) {
    val navBarPadding = windowInsets.asPaddingValues().calculateBottomPadding()
    val statusBarPadding = windowInsets.asPaddingValues().calculateTopPadding()
    var height by remember {
        mutableFloatStateOf(0f)
    }
    val density = LocalDensity.current
    val playerBottomPadding = 4.dp

    var isFullScreen by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(enabled = isFullScreen) {
        if (isFullScreen) {
            isFullScreen = false
        }
    }
    val transition = updateTransition(targetState = isFullScreen, label = "player animation")

    val paddingHorizontal by transition.animateDp(
        transitionSpec = transitionSpec(), label = "padding horizontal"
    ) { targetIsFullScreen ->
        if (targetIsFullScreen) 0.dp else 8.dp
    }

    val paddingBottom by transition.animateDp(
        transitionSpec = transitionSpec(), label = "padding bottom"
    ) { targetIsFullScreen ->
        if (targetIsFullScreen) 0.dp else (playerBottomPadding + navBarPadding)
    }
    val paddingValues by remember {
        derivedStateOf {
            with(density) {
                PaddingValues(bottom = paddingBottom + height.toDp() + 8.dp)
            }
        }
    }
    CompositionLocalProvider(LocalPaddingValues provides paddingValues) {
        Box(modifier = modifier) {
            content()
            mainState.playerViewState?.let {
                AnimatedPlayerCard(state = it,
                    transition = transition,
                    statusBarPadding = statusBarPadding,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = paddingBottom)
                        .padding(horizontal = paddingHorizontal)
                        .onPlaced { coordinates ->
                            height = coordinates.boundsInParent().height
                        }
                        .clickable(enabled = !isFullScreen) {
                            isFullScreen = !isFullScreen
                        },
                    onPreviousButtonClicked = { handle(MainUserAction.PreviousButtonClicked) },
                    onNextButtonClicked = { handle(MainUserAction.NextButtonClicked) },
                    onPlayToggled = { handle(MainUserAction.PlayToggled) },
                    onDismissPlayer = {
                        isFullScreen = false
                    },
                    onProgressBarValueChange = { position, trackLength ->
                        handle(
                            MainUserAction.ProgressBarClicked(
                                position, trackLength
                            )
                        )
                    })
            }
        }
    }
}
