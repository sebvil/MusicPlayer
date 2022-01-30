package com.sebastianvm.musicplayer.ui.bottomsheets.context


import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State

open class BaseContextMenuState(open val listItems: List<ContextMenuItem>, open val menuTitle: String) : State

sealed class BaseContextMenuUserAction : UserAction {
    data class RowClicked(val row: ContextMenuItem) : BaseContextMenuUserAction()
}

abstract class BaseContextMenuViewModel<E : UiEvent, S : BaseContextMenuState>(initialState: S) :
    BaseViewModel<BaseContextMenuUserAction, E, S>(initialState)
