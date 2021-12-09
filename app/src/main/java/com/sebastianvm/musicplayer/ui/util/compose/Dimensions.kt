package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val extraSmall: Dp = 2.dp,
    val small: Dp = 4.dp,
    val mediumSmall: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val mediumLarge: Dp = 16.dp,
    val large: Dp = 32.dp,
)

data class BottomSheet(
    val rowHeight: Dp = 56.dp,
    val startPadding: Dp = 16.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalBottomSheetDimensions = staticCompositionLocalOf { BottomSheet() }

object AppDimensions {
    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    val bottomSheet: BottomSheet
        @Composable
        @ReadOnlyComposable
        get() = LocalBottomSheetDimensions.current
}