package com.sebastianvm.musicplayer.ui.player

data class MinutesSecondsTime(val minutes: Long, val seconds: Long) {
    companion object {
        fun fromMs(ms: Long): MinutesSecondsTime {
            return MinutesSecondsTime((ms / 1000) / 60, (ms / 1000) % 60)
        }
    }
}