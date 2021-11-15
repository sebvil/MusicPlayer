package com.sebastianvm.musicplayer.ui.util.mvvm.state

import androidx.lifecycle.LiveData
import com.sebastianvm.musicplayer.ui.util.mvvm.NonNullMediatorLiveData

class StateStore<S: State>(initialState: S) {

    val stateLiveData = NonNullMediatorLiveData(initialState)


    fun setState(func: S.()-> S) {
        stateLiveData.value = stateLiveData.value.func()
    }
    fun <T: Any> observe(data: LiveData<T>, onChanged: (T) -> Unit) {
        stateLiveData.addSource(data) {  onChanged(it) }

    }
}