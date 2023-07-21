package com.sebastianvm.musicplayer.ui

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ramcosta.composedestinations.DestinationsNavHost
import com.sebastianvm.musicplayer.ui.components.M3ModalBottomSheetLayout
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        setContent {
            M3AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bottomSheetNavigator = rememberBottomSheetNavigator()
                    M3ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
                        AppScreenHost(
                            viewModel = viewModel,
                            onPreviousButtonClicked = { viewModel.handle(MainUserAction.PreviousButtonClicked) },
                            onPlayToggled = { viewModel.handle(MainUserAction.PlayToggled) },
                            onNextButtonClicked = { viewModel.handle(MainUserAction.NextButtonClicked) }) {
                            DestinationsNavHost(navGraph = NavGraphs.root)
                        }
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
