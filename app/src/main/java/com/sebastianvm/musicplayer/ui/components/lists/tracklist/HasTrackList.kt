package com.sebastianvm.musicplayer.ui.components.lists.tracklist

import android.os.Parcelable


interface HasTrackList : Parcelable {
    val args: TrackListComponentArgs
}