package com.sebastianvm.musicplayer.repository.playback

import androidx.annotation.StringRes

sealed class PlaybackResult {
    object Loading : PlaybackResult()
    object Success : PlaybackResult()
    data class Error(@StringRes val errorMessage: Int) : PlaybackResult()
}
