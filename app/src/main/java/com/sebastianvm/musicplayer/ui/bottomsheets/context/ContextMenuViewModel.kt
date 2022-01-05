package com.sebastianvm.musicplayer.ui.bottomsheets.context


import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MEDIA_GROUP
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.AlbumRepository
import com.sebastianvm.musicplayer.repository.ArtistRepository
import com.sebastianvm.musicplayer.repository.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.TrackRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ContextMenuViewModel @Inject constructor(
    initialState: ContextMenuState,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val musicServiceConnection: MusicServiceConnection
) : BaseViewModel<ContextMenuUserAction, ContextMenuUiEvent, ContextMenuState>(initialState) {

    init {
        when (state.value.mediaType) {
            MediaType.ALL_TRACKS, MediaType.SINGLE_TRACK -> {
                collect(trackRepository.getTrack(state.value.mediaId)) {
                    setState {
                        copy(
                            menuTitle = it.track.trackName,
                            listItems = contextMenuItemsForMedia(state.value.mediaType, state.value.mediaGroup.mediaType, it.artists.size)
                        )
                    }
                }
            }
            MediaType.ALBUM -> {
                collect(albumRepository.getAlbum(state.value.mediaId)) {
                    setState {
                        copy(
                            menuTitle = it.album.albumName,
                            listItems = contextMenuItemsForMedia(state.value.mediaType, state.value.mediaGroup.mediaType, it.artists.size)

                        )
                    }
                }
            }
            MediaType.GENRE -> {
                setState {
                    copy(
                        menuTitle = state.value.mediaId,
                        listItems = contextMenuItemsForMedia(state.value.mediaType, state.value.mediaGroup.mediaType)
                    )
                }
            }
            MediaType.ARTIST -> {
                collect(artistRepository.getArtist(state.value.mediaId)) {
                    setState {
                        copy(
                            menuTitle = it.artist.artistName,
                            listItems = contextMenuItemsForMedia(state.value.mediaType, state.value.mediaGroup.mediaType)
                        )
                    }
                }
            }
        }
    }

    override fun handle(action: ContextMenuUserAction) {
        when (action) {
            is ContextMenuUserAction.RowClicked -> {
                when (action.row) {
                    is ContextMenuItem.Play, is ContextMenuItem.PlayFromBeginning, is ContextMenuItem.PlayAllSongs -> {
                        val transportControls = musicServiceConnection.transportControls
                        viewModelScope.launch {
                            val mediaGroup = state.value.mediaGroup
                            mediaQueueRepository.createQueue(
                                mediaGroup = mediaGroup,
                                sortOrder = state.value.sortOrder,
                                sortOption = SortOption.valueOf(state.value.selectedSort)
                            )
                            val extras = Bundle().apply {
                                putParcelable(MEDIA_GROUP, mediaGroup)
                            }
                            transportControls.playFromMediaId(state.value.mediaId, extras)
                            addUiEvent(ContextMenuUiEvent.NavigateToPlayer)
                        }
                    }
                    is ContextMenuItem.ViewAlbum -> {
                        when (state.value.mediaType) {
                            MediaType.ALL_TRACKS, MediaType.SINGLE_TRACK -> {
                                collect(trackRepository.getTrack(state.value.mediaId)) {
                                    addUiEvent(
                                        ContextMenuUiEvent.NavigateToAlbum(it.album.albumId)
                                    )
                                }
                            }
                            MediaType.ALBUM -> {
                                addUiEvent(ContextMenuUiEvent.NavigateToAlbum(state.value.mediaId))
                            }
                            else -> throw UnsupportedOperationException("ViewAlbum is not supported for media type ${state.value.mediaType}")
                        }

                    }
                    is ContextMenuItem.ViewArtists -> {
                        when (state.value.mediaType) {
                            MediaType.ALL_TRACKS, MediaType.SINGLE_TRACK -> {
                                addUiEvent(
                                    ContextMenuUiEvent.NavigateToArtistsBottomSheet(
                                        state.value.mediaId,
                                        state.value.mediaType
                                    )
                                )
                            }
                            MediaType.ALBUM -> {
                                addUiEvent(
                                    ContextMenuUiEvent.NavigateToArtistsBottomSheet(
                                        state.value.mediaId,
                                        state.value.mediaType
                                    )
                                )
                            }
                            else -> throw UnsupportedOperationException("ViewArtists is not supported for media type ${state.value.mediaType}")
                        }
                    }
                    is ContextMenuItem.ViewArtist -> {
                        when (state.value.mediaType) {
                            MediaType.ALL_TRACKS, MediaType.SINGLE_TRACK -> {
                                collect(trackRepository.getTrack(state.value.mediaId)) { track ->
                                    addUiEvent(ContextMenuUiEvent.NavigateToArtist(track.artists[0].artistId))
                                }
                            }
                            MediaType.ALBUM -> {
                                collect(albumRepository.getAlbum(state.value.mediaId)) { album ->
                                    addUiEvent(ContextMenuUiEvent.NavigateToArtist(album.artists[0].artistId))
                                }
                            }
                            MediaType.ARTIST -> {
                                addUiEvent(ContextMenuUiEvent.NavigateToArtist(state.value.mediaId))
                            }
                            else -> throw UnsupportedOperationException("ViewArtists is not supported for media type ${state.value.mediaType}")
                        }
                    }
                    is ContextMenuItem.ViewGenre -> {
                        addUiEvent(ContextMenuUiEvent.NavigateToGenre(genreName = state.value.mediaId))
                    }
                }
            }
        }
    }
}

data class ContextMenuState(
    val mediaId: String,
    val menuTitle: String,
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
            menuTitle = "",
            mediaType = mediaType,
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(),
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
    data class NavigateToAlbum(val albumId: String) : ContextMenuUiEvent()
    data class NavigateToArtist(val artistId: String) : ContextMenuUiEvent()
    data class NavigateToArtistsBottomSheet(val mediaId: String, val mediaType: MediaType) :
        ContextMenuUiEvent()

    data class NavigateToGenre(val genreName: String) : ContextMenuUiEvent()
}

