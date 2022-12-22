package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem

interface MediaModel {
    fun toMediaItem(): MediaItem

}