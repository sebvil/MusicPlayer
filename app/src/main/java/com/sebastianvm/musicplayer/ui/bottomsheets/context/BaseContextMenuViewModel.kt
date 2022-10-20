package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent

abstract class BaseContextMenuState(
    open val listItems: List<ContextMenuItem>,
    open val mediaId: Long,
    open val menuTitle: String,
    open val playbackResult: PlaybackResult? = null
) : State

sealed interface BaseContextMenuUiEvent : UiEvent {
    data class ShowToast(@StringRes val message: Int, val success: Boolean) :
        BaseContextMenuUiEvent

}

sealed interface BaseContextMenuUserAction : UserAction

abstract class BaseContextMenuViewModel<S : BaseContextMenuState>(
    initialState: S
) : BaseViewModel<S, BaseContextMenuUserAction, BaseContextMenuUiEvent>(initialState) {
    abstract fun onRowClicked(row: ContextMenuItem)
    abstract fun onPlaybackErrorDismissed()
    override fun handle(action: BaseContextMenuUserAction) {
        TODO("handle Not yet implemented")
    }
}
