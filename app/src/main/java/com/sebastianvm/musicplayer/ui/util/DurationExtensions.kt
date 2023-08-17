package com.sebastianvm.musicplayer.ui.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun Duration.toDisplayableString(): String {
    return "%02d:%02d".format(inWholeMinutes, (this - inWholeMinutes.minutes).inWholeSeconds)
}
