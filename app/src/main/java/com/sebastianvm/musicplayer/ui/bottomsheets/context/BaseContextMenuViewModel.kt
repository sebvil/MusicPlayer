package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent

abstract class BaseContextMenuState(
    open val listItems: List<ContextMenuItem>,
    open val mediaId: Long,
    open val menuTitle: String,
    open val playbackResult: PlaybackResult? = null
) : State

sealed class BaseContextMenuUiEvent : UiEvent {
    data class ShowToast(@StringRes val message: Int, val success: Boolean) :
        BaseContextMenuUiEvent()

}

abstract class BaseContextMenuViewModel<S : BaseContextMenuState>(
    initialState: S
) : BaseViewModel<BaseContextMenuUiEvent, S>(initialState) {
    abstract fun onRowClicked(row: ContextMenuItem)
    abstract fun onPlaybackErrorDismissed()
}
