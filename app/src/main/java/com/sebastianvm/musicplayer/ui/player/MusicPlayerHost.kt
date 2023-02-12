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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
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
    if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
        MusicPlayerHostCompactLayout(
            musicPlayerViewState = musicPlayerViewState,
            windowWidthSizeClass = windowWidthSizeClass,
            modifier = modifier,
            paddingValues = paddingValues,
            onProgressBarClicked = onProgressBarClicked,
            onPreviousButtonClicked = onPreviousButtonClicked,
            onNextButtonClicked = onNextButtonClicked,
            onPlayToggled = onPlayToggled
        ) {
            content()
        }
    } else {
        MusicPlayerHostExpandedLayout(
            musicPlayerViewState = musicPlayerViewState,
            windowWidthSizeClass = windowWidthSizeClass,
            modifier = modifier,
            paddingValues = paddingValues,
            onProgressBarClicked = onProgressBarClicked,
            onPreviousButtonClicked = onPreviousButtonClicked,
            onNextButtonClicked = onNextButtonClicked,
            onPlayToggled = onPlayToggled
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusicPlayerHostCompactLayout(
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
                    windowWidthSizeClass = windowWidthSizeClass,
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
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusicPlayerHostExpandedLayout(
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
                    windowWidthSizeClass = windowWidthSizeClass,
                    modifier = Modifier
                        .padding(all = cornerSize)
                        .systemBarsPadding(),
                    onProgressBarClicked = onProgressBarClicked,
                    onPreviousButtonClicked = onPreviousButtonClicked,
                    onNextButtonClicked = onNextButtonClicked,
                    onPlayToggled = onPlayToggled,
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


@OptIn(ExperimentalMaterialApi::class)
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
                        ListItem {
                            Text(text = "Item #$it")
                        }
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