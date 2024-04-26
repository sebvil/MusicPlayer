package com.sebastianvm.musicplayer.ui.bottomsheets.context

import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction

data class ContextMenuState(
    val listItems: List<ContextMenuItem>,
    val menuTitle: String,
    val showDeleteConfirmationDialog: Boolean = false
) : State

sealed interface BaseContextMenuUserAction : UserAction {
    data class RowClicked(val row: ContextMenuItem) : BaseContextMenuUserAction
    data object CancelDeleteClicked : BaseContextMenuUserAction
    data object ConfirmDeleteClicked : BaseContextMenuUserAction
}

abstract class BaseContextMenuStateHolder :
    StateHolder<UiState<ContextMenuState>, BaseContextMenuUserAction> {
    protected abstract fun onRowClicked(row: ContextMenuItem)

    // no-ops except for PlaylistContextStateHolder
    protected open fun onCancelDeleteClicked() = Unit
    protected open fun onConfirmDeleteClicked() = Unit

    override fun handle(action: BaseContextMenuUserAction) {
        when (action) {
            is BaseContextMenuUserAction.RowClicked -> onRowClicked(row = action.row)
            is BaseContextMenuUserAction.CancelDeleteClicked -> onCancelDeleteClicked()
            is BaseContextMenuUserAction.ConfirmDeleteClicked -> onConfirmDeleteClicked()
        }
    }
}
