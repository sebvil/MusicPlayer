package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerHost(
    state: MusicPlayerViewState?,
    modifier: Modifier = Modifier,
    onProgressBarClicked: (position: Int) -> Unit,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPlayToggled: () -> Unit,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {

    val player: @Composable () -> Unit = {
        if (state != null) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 8.dp, topStart = 8.dp)),
                tonalElevation = 8.dp
            ) {
                MusicPlayerView(
                    state = state,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .navigationBarsPadding(),
                    onProgressBarClicked = onProgressBarClicked,
                    onPreviousButtonClicked = onPreviousButtonClicked,
                    onNextButtonClicked = onNextButtonClicked,
                    onPlayToggled = onPlayToggled,
                )
            }
        }
    }
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background)
    ) {
        SubcomposeLayout(modifier = modifier) { constraints ->
            val layoutWidth = constraints.maxWidth
            val layoutHeight = constraints.maxHeight

            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            layout(layoutWidth, layoutHeight) {
                val musicPlayerViewPlaceables =
                    subcompose("Player", player).map { it.measure(looseConstraints) }
                val playerHeight =
                    musicPlayerViewPlaceables.maxByOrNull { it.height }?.height ?: 0

                val bodyContentPlaceables = subcompose("Main Content") {
                    val insets = contentWindowInsets.asPaddingValues(this@SubcomposeLayout)
                    val innerPadding = PaddingValues(
                        top = insets.calculateTopPadding(),
                        bottom = playerHeight.toDp(),
                        start = insets.calculateStartPadding((this@SubcomposeLayout).layoutDirection),
                        end = insets.calculateEndPadding((this@SubcomposeLayout).layoutDirection)
                    )
                    content(innerPadding)
                }.map { it.measure(looseConstraints) }

                bodyContentPlaceables.forEach {
                    it.place(0, 0)
                }

                musicPlayerViewPlaceables.forEach {
                    it.place(0, layoutHeight - playerHeight)
                }
            }
        }
    }
}