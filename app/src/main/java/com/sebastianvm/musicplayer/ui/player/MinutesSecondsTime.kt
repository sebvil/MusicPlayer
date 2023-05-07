package com.sebastianvm.musicplayer.ui.player

data class MinutesSecondsTime(val minutes: Long, val seconds: Long) {

    override fun toString(): String {
        return "%02d:%02d".format(minutes, seconds)
    }

    companion object {
        fun fromMs(ms: Long): MinutesSecondsTime {
            return MinutesSecondsTime((ms / 1000) / 60, (ms / 1000) % 60)
        }
    }
}