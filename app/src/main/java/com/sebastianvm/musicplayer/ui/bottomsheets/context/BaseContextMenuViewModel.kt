package com.sebastianvm.musicplayer.ui.bottomsheets.context


import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State

open class BaseContextMenuState(
    open val listItems: List<ContextMenuItem>,
    open val menuTitle: String
) : State

sealed class BaseContextMenuUiEvent : UiEvent {
    object NavigateToPlayer : BaseContextMenuUiEvent()
    data class NavigateToAlbum(val albumId: String) : BaseContextMenuUiEvent()
    data class NavigateToArtist(val artistName: String) : BaseContextMenuUiEvent()
    data class NavigateToArtistsBottomSheet(val mediaId: String, val mediaType: MediaType) :
        BaseContextMenuUiEvent()

    data class NavigateToGenre(val genreName: String) : BaseContextMenuUiEvent()
}

sealed class BaseContextMenuUserAction : UserAction {
    data class RowClicked(val row: ContextMenuItem) : BaseContextMenuUserAction()
}

abstract class BaseContextMenuViewModel<S : BaseContextMenuState>(initialState: S) :
    BaseViewModel<BaseContextMenuUserAction, BaseContextMenuUiEvent, S>(initialState)
