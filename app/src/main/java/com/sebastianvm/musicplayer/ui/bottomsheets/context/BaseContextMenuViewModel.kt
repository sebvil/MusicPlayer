package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.player.MediaType
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
    object NavigateToPlayer : BaseContextMenuUiEvent()
    data class NavigateToAlbum(val albumId: Long) : BaseContextMenuUiEvent()
    data class NavigateToArtist(val artistId: Long) : BaseContextMenuUiEvent()
    data class NavigateToArtistsBottomSheet(val mediaId: Long, val mediaType: MediaType) :
        BaseContextMenuUiEvent()

    data class NavigateToGenre(val genreId: Long) : BaseContextMenuUiEvent()
    data class NavigateToPlaylist(val playlistId: Long) : BaseContextMenuUiEvent()
    data class ShowToast(@StringRes val message: Int, val success: Boolean) :
        BaseContextMenuUiEvent()

    object HideBottomSheet : BaseContextMenuUiEvent()
}

abstract class BaseContextMenuViewModel<S : BaseContextMenuState>(
    initialState: S
) : BaseViewModel<BaseContextMenuUiEvent, S>(initialState) {
    abstract fun onRowClicked(row: ContextMenuItem)
    abstract fun onPlaybackErrorDismissed()
}
