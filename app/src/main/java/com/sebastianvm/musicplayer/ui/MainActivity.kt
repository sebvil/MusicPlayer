package com.sebastianvm.musicplayer.ui

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import com.sebastianvm.musicplayer.MusicPlayerApplication
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.components.M3ModalBottomSheetLayout
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        (this.application as MusicPlayerApplication).viewModelFactory
    }

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
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
                    val navController = rememberNavController()
                    navController.navigatorProvider += bottomSheetNavigator
                    M3ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
                        val state by viewModel.state.collectAsState()
                        AppScreenHost(
                            mainState = state,
                            onPreviousButtonClicked = { viewModel.handle(MainUserAction.PreviousButtonClicked) },
                            onPlayToggled = { viewModel.handle(MainUserAction.PlayToggled) },
                            onNextButtonClicked = { viewModel.handle(MainUserAction.NextButtonClicked) },
                            onProgressBarValueChange = { progress ->
                                viewModel.handle(
                                    MainUserAction.ProgressBarClicked(progress)
                                )
                            }
                        ) {
                            DestinationsNavHost(
                                navGraph = NavGraphs.root,
                                navController = navController,
                                engine = rememberAnimatedNavHostEngine(),
                                dependenciesContainerBuilder = {
                                    val handlePlayback =
                                        PlaybackHandler { mediaGroup: MediaGroup, initialTrackIndex: Int ->
                                            viewModel.handle(
                                                MainUserAction.PlayMedia(
                                                    mediaGroup,
                                                    initialTrackIndex
                                                )
                                            )
                                        }
                                    dependency(handlePlayback)
                                }
                            )
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
