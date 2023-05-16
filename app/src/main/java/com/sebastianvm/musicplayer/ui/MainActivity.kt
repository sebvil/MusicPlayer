package com.sebastianvm.musicplayer.ui

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.sebastianvm.musicplayer.ui.navigation.AppNavHost
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()
            val useDarkIcons = !isSystemInDarkTheme()
            SideEffect {
                WindowCompat.getInsetsController(
                    window,
                    window.decorView
                ).apply {
                    isAppearanceLightStatusBars = useDarkIcons
                    isAppearanceLightNavigationBars = true
                }
            }
            M3AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreenHost(
                        state = state.playerViewState,
                        onPreviousButtonClicked = { viewModel.handle(MainUserAction.PreviousButtonClicked) },
                        onPlayToggled = { viewModel.handle(MainUserAction.PlayToggled) },
                        onNextButtonClicked = { viewModel.handle(MainUserAction.NextButtonClicked) }) {
                        val navController = rememberNavController()
                        AppNavHost(navController = navController)
                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.handle(MainUserAction.ConnectToMusicService)
    }

    override fun onStop() {
        super.onStop()
        viewModel.handle(MainUserAction.DisconnectFromMusicService)
    }

}
