package com.sebastianvm.musicplayer.player

import android.net.Uri

data class CurrentPlaybackInfo(val currentQueue: MediaGroup, val currentItemUri: Uri)
