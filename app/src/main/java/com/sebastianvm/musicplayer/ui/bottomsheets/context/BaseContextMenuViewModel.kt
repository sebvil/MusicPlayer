package com.sebastianvm.musicplayer.ui.bottomsheets.context


import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction

abstract class BaseContextMenuState(
    open val listItems: List<ContextMenuItem>,
    open val mediaId: Long,
    open val menuTitle: String,
    open val playbackResult: PlaybackResult? = null
) : State


sealed interface BaseContextMenuUserAction : UserAction {
    data class RowClicked(val row: ContextMenuItem) : BaseContextMenuUserAction
    object DismissPlaybackErrorDialog : BaseContextMenuUserAction
    object CancelDeleteClicked : BaseContextMenuUserAction
    object ConfirmDeleteClicked : BaseContextMenuUserAction
}

abstract class BaseContextMenuViewModel<S : BaseContextMenuState>(
    initialState: S
) : BaseViewModel<S, BaseContextMenuUserAction>(initialState) {
    protected abstract fun onRowClicked(row: ContextMenuItem)
    protected abstract fun onPlaybackErrorDismissed()

    // no-ops except for PlaylistContextMenuViewModel
    protected open fun onCancelDeleteClicked() = Unit
    protected open fun onConfirmDeleteClicked() = Unit


    override fun handle(action: BaseContextMenuUserAction) {
        when (action) {
            is BaseContextMenuUserAction.RowClicked -> onRowClicked(row = action.row)
            is BaseContextMenuUserAction.DismissPlaybackErrorDialog -> onPlaybackErrorDismissed()
            is BaseContextMenuUserAction.CancelDeleteClicked -> onCancelDeleteClicked()
            is BaseContextMenuUserAction.ConfirmDeleteClicked -> onConfirmDeleteClicked()
        }
    }
}
