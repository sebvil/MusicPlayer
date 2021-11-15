package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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


@Composable
fun AppScreen(screen: @Composable () -> Unit) {
    AppScreen { _, contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            screen()
        }
    }
}