package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.sebastianvm.musicplayer.MusicPlayerApplication

@Composable
inline fun <reified VM : ViewModel> viewModel(): VM =
    androidx.lifecycle.viewmodel.compose.viewModel(
        VM::class.java,
        factory = (LocalContext.current.applicationContext as MusicPlayerApplication).viewModelFactory,
    )
