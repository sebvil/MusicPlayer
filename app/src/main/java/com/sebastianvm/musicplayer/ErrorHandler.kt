package com.sebastianvm.musicplayer

import android.util.Log

class ErrorHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.i("ERROR", "Caught an error in thread $t: $e")
    }
}