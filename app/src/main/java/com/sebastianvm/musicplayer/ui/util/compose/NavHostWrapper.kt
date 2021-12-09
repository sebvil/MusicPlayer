package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sebastianvm.musicplayer.ui.theme.AppTheme
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavHostWrapper(navHost: @Composable (NavHostController) -> Unit) {
    AppTheme {
        M3AppTheme {
            val navController = rememberNavController()
            navHost(navController)
        }
    }
}


