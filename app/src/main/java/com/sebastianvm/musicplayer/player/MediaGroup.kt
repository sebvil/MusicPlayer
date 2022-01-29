package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaGroup(val mediaType: MediaType, val mediaId: String) : Parcelable
