package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sebastianvm.musicplayer.ui.navigation.BottomNavBar
import com.sebastianvm.musicplayer.ui.theme.AppTheme
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(screen: @Composable (NavHostController, PaddingValues) -> Unit) {
    AppTheme {
        M3AppTheme {
            val navController = rememberNavController()
            Scaffold(
                bottomBar = { BottomNavBar(navController = navController) },
            ) { contentPadding ->
                screen(navController, contentPadding)
            }
        }
    }
}


data class Spacing(
    val extraSmall: Dp = 2.dp,
    val small: Dp = 4.dp,
    val mediumSmall: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val mediumLarge: Dp = 16.dp,
    val large: Dp = 32.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

object AppDimensions {
    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current
}