package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


data class TrackContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val mediaId: String,
    val mediaGroup: MediaGroup,
    val selectedSort: SortOption,
    val sortOrder: SortOrder
) : BaseContextMenuState(listItems = listItems, menuTitle = menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialTrackContextMenuStateProvider(savedStateHandle: SavedStateHandle): TrackContextMenuState {
        val mediaId = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val mediaGroupType =
            MediaGroupType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        val mediaGroupMediaId = savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_ID)!!
        val selectedSort = savedStateHandle.get<String>(NavArgs.SORT_OPTION)!!
        val sortOrder = savedStateHandle.get<String>(NavArgs.SORT_ORDER)!!
        return TrackContextMenuState(
            mediaId = mediaId,
            menuTitle = "",
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(),
            selectedSort = SortOption.valueOf(selectedSort),
            sortOrder = SortOrder.valueOf(sortOrder)
        )
    }
}

sealed class TrackContextMenuUiEvent : UiEvent {
    object NavigateToPlayer : TrackContextMenuUiEvent()
    data class NavigateToAlbum(val albumId: String) : TrackContextMenuUiEvent()
    data class NavigateToArtist(val artistName: String) : TrackContextMenuUiEvent()
    data class NavigateToArtistsBottomSheet(val mediaId: String, val mediaType: MediaType) :
        TrackContextMenuUiEvent()
}


class TrackContextMenuViewModel(
    initialState: TrackContextMenuState,
    trackRepository: TrackRepository,
) : BaseContextMenuViewModel<TrackContextMenuUiEvent, TrackContextMenuState>(initialState) {
    init {
        collect(trackRepository.getTrack(state.value.mediaId)) {
            setState {
                copy(
                    menuTitle = it.track.trackName,
                    listItems = contextMenuItemsForMedia(
                        MediaType.TRACK,
                        state.value.mediaGroup.mediaGroupType,
                        it.artists.size
                    )
                )
            }
        }
    }
    override fun handle(action: BaseContextMenuUserAction) {
        TODO("Not yet implemented")
    }
}