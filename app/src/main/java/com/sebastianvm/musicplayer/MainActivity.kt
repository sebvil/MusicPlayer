package com.sebastianvm.musicplayer

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sebastianvm.musicplayer.ui.navigation.AppNavHost
import com.sebastianvm.musicplayer.ui.util.compose.NavHostWrapper
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
            }
            NavHostWrapper { navController ->
                AppNavHost(navController = navController)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.handle(MainActivityUserAction.ConnectToMusicService)
    }

    override fun onStop() {
        super.onStop()
        viewModel.handle(MainActivityUserAction.DisconnectFromMusicService)
    }

}
