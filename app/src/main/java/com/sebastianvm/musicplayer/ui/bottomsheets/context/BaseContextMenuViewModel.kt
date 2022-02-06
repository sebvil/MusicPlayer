package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.annotation.StringRes
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
    data class NavigateToPlaylist(val playlistName: String) : BaseContextMenuUiEvent()
    data class ShowToast(@StringRes val message: Int, val success: Boolean) :
        BaseContextMenuUiEvent()

    object HideBottomSheet : BaseContextMenuUiEvent()
}

object BaseContextMenuUserAction : UserAction

abstract class BaseContextMenuViewModel<S : BaseContextMenuState>(
    initialState: S
) : BaseViewModel<BaseContextMenuUserAction, BaseContextMenuUiEvent, S>(initialState) {
    override fun handle(action: BaseContextMenuUserAction) = Unit
    abstract fun onRowClicked(row: ContextMenuItem)
}
