package com.sebastianvm.musicplayer.util.extensions

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle

fun <T : Parcelable> SavedStateHandle.getArgs(): T {
    return get("args")!!
}