package com.sebastianvm.musicplayer.ui.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun WindowWidthSizeClass.toPlayerViewMode(isExpanded: Boolean): PlayerViewMode = when {
    isExpanded -> PlayerViewMode.FULL_SCREEN
    this == WindowWidthSizeClass.Compact -> PlayerViewMode.BOTTOM_BAR
    else -> PlayerViewMode.SIDE_BAR
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MusicPlayerHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val musicPlayerViewModel: MusicPlayerViewModel = viewModel()
    val state by musicPlayerViewModel.stateFlow.collectAsStateWithLifecycle()
    val musicPlayerViewState = state.musicPlayerViewState
    val windowWidthSizeClass =
        calculateWindowSizeClass(activity = LocalContext.current.findActivity()).widthSizeClass
    Scaffold { paddingValues ->
        MusicPlayerHostLayout(
            musicPlayerViewState = musicPlayerViewState,
            windowWidthSizeClass = windowWidthSizeClass,
            modifier = modifier,
            content = content,
            onPlayToggled = { musicPlayerViewModel.handle(MusicPlayerUserAction.PlayToggled) },
            onNextButtonClicked = { musicPlayerViewModel.handle(MusicPlayerUserAction.NextButtonClicked) },
            onPreviousButtonClicked = {
                musicPlayerViewModel.handle(
                    MusicPlayerUserAction.PreviousButtonClicked
                )
            },
            onProgressBarClicked = {
                musicPlayerViewModel.handle(
                    MusicPlayerUserAction.ProgressBarClicked(
                        it
                    )
                )
            },
            paddingValues = paddingValues

        )
    }
}

@Composable
fun MusicPlayerHostLayout(
    musicPlayerViewState: MusicPlayerViewState?,
    windowWidthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    content: @Composable () -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    when (windowWidthSizeClass.toPlayerViewMode(isExpanded)) {
        PlayerViewMode.BOTTOM_BAR -> {
            BottomBarPlayerLayout(
                musicPlayerViewState = musicPlayerViewState,
                modifier = modifier,
                paddingValues = paddingValues,
                onProgressBarClicked = onProgressBarClicked,
                onPreviousButtonClicked = onPreviousButtonClicked,
                onNextButtonClicked = onNextButtonClicked,
                onPlayToggled = onPlayToggled,
                onToggleExpandedState = { isExpanded = true }
            ) {
                content()
            }
        }

        PlayerViewMode.SIDE_BAR -> {
            SideBarPlayerLayout(
                musicPlayerViewState = musicPlayerViewState,
                modifier = modifier,
                paddingValues = paddingValues,
                onProgressBarClicked = onProgressBarClicked,
                onPreviousButtonClicked = onPreviousButtonClicked,
                onNextButtonClicked = onNextButtonClicked,
                onPlayToggled = onPlayToggled,
                onToggleExpandedState = { isExpanded = true }
            ) {
                content()
            }
        }

        PlayerViewMode.FULL_SCREEN -> {
            checkNotNull(musicPlayerViewState)
            FullScreenPlayerLayout(
                musicPlayerViewState = musicPlayerViewState,
                paddingValues = paddingValues,
                onProgressBarClicked = onProgressBarClicked,
                onPreviousButtonClicked = onPreviousButtonClicked,
                onNextButtonClicked = onNextButtonClicked,
                onPlayToggled = onPlayToggled,
                onToggleExpandedState = { isExpanded = false }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BottomBarPlayerLayout(
    musicPlayerViewState: MusicPlayerViewState?,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    onToggleExpandedState: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = if (musicPlayerViewState == null) paddingValues.calculateBottomPadding() else 0.dp,
                )
                .consumeWindowInsets(paddingValues)
        ) {
            content()

        }
        if (musicPlayerViewState != null) {
            val cornerSize = 16.dp
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = cornerSize, topStart = cornerSize)),
                tonalElevation = 8.dp
            ) {
                MusicPlayerView(
                    state = musicPlayerViewState,
                    playerViewMode = PlayerViewMode.BOTTOM_BAR,
                    modifier = Modifier
                        .padding(
                            top = cornerSize,
                            start = cornerSize,
                            end = cornerSize,
                            bottom = 8.dp
                        )
                        .navigationBarsPadding(),
                    onProgressBarClicked = onProgressBarClicked,
                    onPreviousButtonClicked = onPreviousButtonClicked,
                    onNextButtonClicked = onNextButtonClicked,
                    onPlayToggled = onPlayToggled,
                    onToggleExpandedState = onToggleExpandedState
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SideBarPlayerLayout(
    musicPlayerViewState: MusicPlayerViewState?,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    onToggleExpandedState: () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        if (musicPlayerViewState != null) {
            val cornerSize = 16.dp

            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = cornerSize,
                            bottomEnd = cornerSize
                        )
                    )
                    .widthIn(max = 300.dp)
                    .fillMaxHeight()
            ) {
                MusicPlayerView(
                    state = musicPlayerViewState,
                    playerViewMode = PlayerViewMode.SIDE_BAR,
                    modifier = Modifier
                        .padding(all = cornerSize)
                        .systemBarsPadding(),
                    onProgressBarClicked = onProgressBarClicked,
                    onPreviousButtonClicked = onPreviousButtonClicked,
                    onNextButtonClicked = onNextButtonClicked,
                    onPlayToggled = onPlayToggled,
                    onToggleExpandedState = onToggleExpandedState
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FullScreenPlayerLayout(
    musicPlayerViewState: MusicPlayerViewState,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    onToggleExpandedState: () -> Unit,
) {
    Surface(
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        MusicPlayerView(
            state = musicPlayerViewState,
            playerViewMode = PlayerViewMode.SIDE_BAR,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            onProgressBarClicked = onProgressBarClicked,
            onPreviousButtonClicked = onPreviousButtonClicked,
            onNextButtonClicked = onNextButtonClicked,
            onPlayToggled = onPlayToggled,
            onToggleExpandedState = onToggleExpandedState
        )
    }
}


@ScreenPreview
@Composable
fun MusicPlayerHostPreview(
    @PreviewParameter(
        MusicPlayerViewStatePreviewParameterProvider::class,
        limit = 1
    ) state: MusicPlayerViewState
) {
    ThemedPreview {
        MusicPlayerHostLayout(
            musicPlayerViewState = state,
            windowWidthSizeClass = WindowWidthSizeClass.Expanded,
            content = {
                LazyColumn(modifier = Modifier) {
                    items(50) {
                        ListItem(headlineContent = {
                            Text(text = "Item #$it")
                        })
                    }
                }
            },
            onProgressBarClicked = {},
            onPreviousButtonClicked = {},
            onNextButtonClicked = {},
            onPlayToggled = {},
            paddingValues = PaddingValues()
        )
    }
}