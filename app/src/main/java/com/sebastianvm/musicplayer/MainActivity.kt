package com.sebastianvm.musicplayer

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.sebastianvm.musicplayer.ui.navigation.AppNavHost
import com.sebastianvm.musicplayer.ui.util.compose.NavHostWrapper
import com.sebastianvm.musicplayer.ui.util.images.ThumbnailFetcher
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var thumbnailFetcher: ThumbnailFetcher

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        super.onCreate(savedInstanceState)
        setContent {
            NavHostWrapper(thumbnailFetcher) { navController ->
                AppNavHost(navController = navController)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.connectToMusicService()
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnectFromMusicService()
    }
}
