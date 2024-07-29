package com.sebastianvm.musicplayer.features.main

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.designsystems.components.LocalListItemContainerColor
import com.sebastianvm.musicplayer.core.designsystems.theme.M3AppTheme
import com.sebastianvm.musicplayer.core.services.HasServices
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.mvvm.currentState

class MainActivity : ComponentActivity() {

    private val services: Services by lazy { (application as HasServices).services }

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory(services) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        StrictMode.setThreadPolicy(ThreadPolicy.Builder().detectAll().penaltyLog().build())

        setContent {
            M3AppTheme {
                CompositionLocalProvider(
                    LocalListItemContainerColor provides MaterialTheme.colorScheme.background
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        val state by viewModel.currentState
                        MainApp(state = state, handle = viewModel::handle)
                    }
                }
            }
        }
    }
}
