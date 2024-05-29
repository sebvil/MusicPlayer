package com.sebastianvm.musicplayer.features.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.designsystem.components.BottomSheet
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState
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
    state: AppNavigationState, handle: Handler<AppNavigationAction>, modifier: Modifier = Modifier
) {
    val backStack = state.backStack
    BackHandler(backStack.size > 1) {
        handle(AppNavigationAction.PopBackStack)
    }

    val screens = backStack.getScreensByMode(NavOptions.PresentationMode.Screen)
    val saveableStateHolder = rememberSaveableStateHolder()
    AnimatedContent(targetState = screens, label = "screens", transitionSpec = {
        if (this.targetState.size > this.initialState.size) {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(ANIMATION_DURATION_MS)
            ).togetherWith(
                fadeOut(tween(ANIMATION_DURATION_MS))
            )
        } else {
            (EnterTransition.None).togetherWith(
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION_MS)
                )
            ).apply { targetContentZIndex = -1f }
        }
    }) {
        it.lastOrNull()?.Content(saveableStateHolder = saveableStateHolder, modifier = modifier)
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
                target?.Content(
                    saveableStateHolder = saveableStateHolder,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }
}

fun List<BackStackEntry>.getScreensByMode(mode: NavOptions.PresentationMode): List<Screen<*, *>> {
    return this.filter { it.presentationMode == mode }.map { it.screen }
}

private const val ANIMATION_DURATION_MS = 500
