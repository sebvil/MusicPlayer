package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.MediatorLiveData

open class NonNullMediatorLiveData<T: Any>(initialValue: T) : MediatorLiveData<T>() {

    init {
        value = initialValue
    }

    override fun getValue(): T {
        return super.getValue() as T
    }
}