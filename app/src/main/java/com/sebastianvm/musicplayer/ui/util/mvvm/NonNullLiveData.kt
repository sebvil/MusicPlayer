package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.LiveData

open class NonNullLiveData <T: Any>(value: T) : LiveData<T>(value) {
    override fun getValue(): T {
        return super.getValue() as T
    }
}