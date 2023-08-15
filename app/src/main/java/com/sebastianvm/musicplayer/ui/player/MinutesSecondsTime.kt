package com.sebastianvm.musicplayer.ui.player

data class MinutesSecondsTime(val minutes: Long, val seconds: Long) {

    override fun toString(): String {
        return "%02d:%02d".format(minutes, seconds)
    }

    companion object {
        private const val MS_PER_SECOND = 1000
        private const val SECONDS_PER_MINUTE = 60
        fun fromMs(ms: Long): MinutesSecondsTime {
            return MinutesSecondsTime(
                minutes = (ms / MS_PER_SECOND) / SECONDS_PER_MINUTE,
                seconds = (ms / MS_PER_SECOND) % SECONDS_PER_MINUTE
            )
        }
    }
}
