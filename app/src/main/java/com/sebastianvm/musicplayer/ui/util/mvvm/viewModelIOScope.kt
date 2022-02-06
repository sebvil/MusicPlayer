package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ViewModel.launchViewModelIOScope(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch {
        withContext(Dispatchers.IO) {
            block()
        }
    }
}