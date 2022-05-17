package com.sebastianvm.musicplayer.util.extensions

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.ui.navigation.ARGS

fun <T : Parcelable> SavedStateHandle.getArgs(): T {
    return get(ARGS)!!
}