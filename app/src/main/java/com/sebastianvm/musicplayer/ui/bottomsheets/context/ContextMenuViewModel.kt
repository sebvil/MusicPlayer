package com.sebastianvm.musicplayer.ui.bottomsheets.context


import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MEDIA_GROUP
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.SORT_BY
import com.sebastianvm.musicplayer.player.SORT_ORDER
import com.sebastianvm.musicplayer.repository.TrackRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class ContextMenuViewModel @Inject constructor(
    initialState: ContextMenuState,
    private val trackRepository: TrackRepository,
    private val musicServiceConnection: MusicServiceConnection
) : BaseViewModel<ContextMenuUserAction, ContextMenuUiEvent, ContextMenuState>(initialState) {

    override fun handle(action: ContextMenuUserAction) {
        when (action) {
            is ContextMenuUserAction.RowClicked -> {
                when (action.row) {
                    is ContextMenuItem.Play -> {
                        val transportControls = musicServiceConnection.transportControls
                        val extras = Bundle().apply {
                            putParcelable(MEDIA_GROUP, state.value.mediaGroup)
                            putString(
                                SORT_BY,
                                state.value.selectedSort
                            )
                            putString(SORT_ORDER, state.value.sortOrder.name)
                        }
                        transportControls.playFromMediaId(state.value.mediaId, extras)
                        addUiEvent(ContextMenuUiEvent.NavigateToPlayer)
                    }
                    is ContextMenuItem.ViewAlbum -> {
                        collect(trackRepository.getTrack(state.value.mediaId)) {
                            addUiEvent(
                                ContextMenuUiEvent.NavigateToAlbum(it.album.albumGid)
                            )
                        }
                    }
                    ContextMenuItem.PlayAllSongs -> TODO()
                    ContextMenuItem.PlayFromBeginning -> TODO()
                    ContextMenuItem.ViewArtists -> {
                        when (state.value.mediaType) {
                            MediaType.TRACK -> {
                                collect(trackRepository.getTrack(state.value.mediaId)) {
                                    if (it.artists.size == 1) {
                                        val artist = it.artists[0]
                                        addUiEvent(
                                            ContextMenuUiEvent.NavigateToArtist(artist.artistGid)
                                        )
                                    } else {
                                        addUiEvent(
                                            ContextMenuUiEvent.NavigateToArtistsBottomSheet(
                                                state.value.mediaId,
                                                state.value.mediaType
                                            )
                                        )
                                    }
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

data class ContextMenuState(
    val mediaId: String,
    val mediaType: MediaType,
    val mediaGroup: MediaGroup,
    val listItems: List<ContextMenuItem>,
    val selectedSort: String,
    val sortOrder: SortOrder
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialContextMenuStateProvider(savedStateHandle: SavedStateHandle): ContextMenuState {
        val mediaId = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val mediaType = MediaType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_TYPE)!!)
        val mediaGroupType =
            MediaType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        val mediaGroupMediaId = savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_ID)!!
        val selectedSort = savedStateHandle.get<String>(NavArgs.SORT_OPTION)!!
        val sortOrder = savedStateHandle.get<String>(NavArgs.SORT_ORDER)!!
        return ContextMenuState(
            mediaId = mediaId,
            mediaType = mediaType,
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = contextMenuItemsForMediaType(mediaType),
            selectedSort = selectedSort,
            sortOrder = SortOrder.valueOf(sortOrder)
        )
    }
}


sealed class ContextMenuUserAction : UserAction {
    data class RowClicked(val row: ContextMenuItem) : ContextMenuUserAction()
}

sealed class ContextMenuUiEvent : UiEvent {
    object NavigateToPlayer : ContextMenuUiEvent()
    data class NavigateToAlbum(val albumGid: String) : ContextMenuUiEvent()
    data class NavigateToArtist(val artistGid: String) : ContextMenuUiEvent()
    data class NavigateToArtistsBottomSheet(val mediaId: String, val mediaType: MediaType) :
        ContextMenuUiEvent()
}

