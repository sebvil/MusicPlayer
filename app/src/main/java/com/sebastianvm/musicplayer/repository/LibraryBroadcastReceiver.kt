package com.sebastianvm.musicplayer.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sebastianvm.musicplayer.repository.LibraryScanService.Companion.STOP_SCAN_SERVICE

class LibraryBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("RECEIVER", "Stopping serve")
        if (intent.action == STOP_SCAN_SERVICE) {
            Intent(context, LibraryScanService::class.java).also { stopServiceIntent ->
                context.stopService(stopServiceIntent)
            }
        }
    }
}