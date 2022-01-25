package com.sebastianvm.musicplayer

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringDef
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.content.ContextCompat
import com.sebastianvm.musicplayer.ui.navigation.AppNavHost
import com.sebastianvm.musicplayer.ui.util.compose.NavHostWrapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var errorHandler: ErrorHandler

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(errorHandler)
        }
        setContent {
            NavHostWrapper { navController ->
                AppNavHost(
                    navController = navController,
                    requestPermission = ::requestPermission,
                )
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


    @PermissionStatus
    private fun requestPermission(
        permission: String
    ): String {
        return when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                PERMISSION_GRANTED
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                SHOULD_SHOW_EXPLANATION
            }
            else -> {
                SHOULD_REQUEST_PERMISSION
            }
        }
    }
}

@StringDef(
    PERMISSION_GRANTED,
    SHOULD_SHOW_EXPLANATION,
    SHOULD_REQUEST_PERMISSION
)
annotation class PermissionStatus

const val PERMISSION_GRANTED = "PERMISSION_GRANTED"
const val SHOULD_SHOW_EXPLANATION = "SHOULD_SHOW_EXPLANATION"
const val SHOULD_REQUEST_PERMISSION = "SHOULD_REQUEST_PERMISSION"
