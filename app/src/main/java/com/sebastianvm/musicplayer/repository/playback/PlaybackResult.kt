package com.sebastianvm.musicplayer.repository.playback

import androidx.annotation.StringRes

sealed interface PlaybackResult {
    data object Loading : PlaybackResult
    data object Success : PlaybackResult
    data class Error(@StringRes val errorMessage: Int) : PlaybackResult
}
