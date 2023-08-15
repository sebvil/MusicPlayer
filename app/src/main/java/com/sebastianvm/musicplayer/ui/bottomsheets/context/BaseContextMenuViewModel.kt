package com.sebastianvm.musicplayer.ui.bottomsheets.context

import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction

data class ContextMenuState(
    val listItems: List<ContextMenuItem>,
    val menuTitle: String,
    val playbackResult: PlaybackResult? = null,
    val showDeleteConfirmationDialog: Boolean = false
) : State

sealed interface BaseContextMenuUserAction : UserAction {
    data class RowClicked(val row: ContextMenuItem) : BaseContextMenuUserAction
    data object DismissPlaybackErrorDialog : BaseContextMenuUserAction
    data object CancelDeleteClicked : BaseContextMenuUserAction
    data object ConfirmDeleteClicked : BaseContextMenuUserAction
}

abstract class BaseContextMenuViewModel :
    BaseViewModel<ContextMenuState, BaseContextMenuUserAction>() {
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
