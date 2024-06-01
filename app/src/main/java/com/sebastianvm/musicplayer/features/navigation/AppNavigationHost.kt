package com.sebastianvm.musicplayer.features.navigation

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.designsystem.components.BottomSheet
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun AppNavigationHost(stateHolder: AppNavigationHostStateHolder, modifier: Modifier = Modifier) {
    val state by stateHolder.currentState
    rememberSaveableStateHolder().SaveableStateProvider(key = "main") {
        AppNavigationHost(state, stateHolder::handle, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationHost(
    state: AppNavigationState,
    handle: Handler<AppNavigationAction>,
    modifier: Modifier = Modifier
) {
    val backStack = state.backStack
    val saveableStateHolder = rememberSaveableStateHolder()

    var lastBackStack by remember { mutableStateOf(backStack) }
    LaunchedEffect(backStack) {
        if (backStack.size < lastBackStack.size) {
            val poppedScreen = lastBackStack.last()
            saveableStateHolder.removeState(poppedScreen.uiComponent.key)
        }
        lastBackStack = backStack
    }

    var progress by remember { mutableFloatStateOf(0f) }
    var inPredictiveBack by remember { mutableStateOf(false) }

    PredictiveBackHandler(backStack.size > 1) { backEvent ->
        progress = 0f
        try {
            backEvent.collect {
                inPredictiveBack = true
                progress = it.progress
            }
            inPredictiveBack = false
            handle(AppNavigationAction.PopBackStack)
        } catch (e: CancellationException) {
            inPredictiveBack = false
        }
    }

    val screens = backStack.getScreensByMode(NavOptions.PresentationMode.Screen)

    val transitionState = remember {
        // The state returned here cannot be nullable cause it produces the input of the
        // transitionSpec passed into the AnimatedContent and that must match the non-nullable
        // scope exposed by the transitions on the NavHost and composable APIs.
        SeekableTransitionState(screens)
    }

    if (inPredictiveBack) {
        LaunchedEffect(progress) {
            transitionState.seekTo(progress, screens.dropLast(1))
        }
    } else {
        LaunchedEffect(screens) {
            // This ensures we don't animate after the back gesture is cancelled and we
            // are already on the current state
            if (transitionState.currentState != screens) {
                transitionState.animateTo(screens)
            }
        }
    }

    val transition = rememberTransition(transitionState, label = "backstack")

    transition.AnimatedContent(
        transitionSpec = {
            val easing = CubicBezierEasing(a = 0.1f, b = 0.1f, c = 0f, d = 1f)
            val fadeThreshold = ANIMATION_DURATION_MS * 35 / 100

            val exitAnimationSpec = tween<Float>(durationMillis = fadeThreshold, easing = easing)
            val enterAnimationSpec = tween<Float>(
                durationMillis = ANIMATION_DURATION_MS - fadeThreshold,
                easing = easing,
                delayMillis = fadeThreshold
            )
            if (this.targetState.size > this.initialState.size) {
                (
                        scaleIn(
                            animationSpec = enterAnimationSpec,
                            initialScale = 0.9f
                        ) + fadeIn(
                            animationSpec = enterAnimationSpec
                        )
                        ).togetherWith(
                        scaleOut(
                            animationSpec = exitAnimationSpec,
                            targetScale = 1.1f
                        ) + fadeOut(
                            animationSpec = exitAnimationSpec
                        )
                    )
            } else {
                (
                        fadeIn(
                            animationSpec = enterAnimationSpec
                        ) + scaleIn(
                            animationSpec = enterAnimationSpec,
                            initialScale = 1.1f
                        )
                        ).togetherWith(
                        scaleOut(
                            animationSpec = exitAnimationSpec,
                            targetScale = 0.9f
                        ) + fadeOut(
                            animationSpec = exitAnimationSpec
                        )
                    )
            }.apply {
                targetContentZIndex = targetState.size.toFloat()
            }
        }
    ) {
        it.lastOrNull()?.let { screen ->
            saveableStateHolder.SaveableStateProvider(screen.key) {
                screen.Content(modifier = modifier)
            }
        }
    }

    val bottomSheets = backStack.getScreensByMode(NavOptions.PresentationMode.BottomSheet)
    val sheetState = rememberModalBottomSheetState()
    val target by remember(bottomSheets) {
        mutableStateOf(bottomSheets.lastOrNull())
    }

    var current by remember {
        mutableStateOf(bottomSheets.lastOrNull())
    }

    LaunchedEffect(target) {
        launch {
            if (sheetState.isVisible) {
                sheetState.hide()
            }
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                current = target
            }
        }
    }

    LaunchedEffect(key1 = current) {
        if (!sheetState.isVisible) {
            sheetState.show()
        }
    }

    val showBottomSheet = current != null

    if (showBottomSheet) {
        CompositionLocalProvider(LocalPaddingValues provides PaddingValues()) {
            BottomSheet(
                onDismissRequest = {
                    handle(AppNavigationAction.PopBackStack)
                },
                sheetState = sheetState
            ) {
                current?.let { screen ->
                    saveableStateHolder.SaveableStateProvider(screen.key) {
                        screen.Content(modifier = modifier)
                    }
                }
            }
        }
    }
}

fun List<BackStackEntry>.getScreensByMode(mode: NavOptions.PresentationMode): List<UiComponent<*, *>> {
    return this.filter { it.presentationMode == mode }.map { it.uiComponent }
}

private const val ANIMATION_DURATION_MS = AnimationConstants.DefaultDurationMillis
