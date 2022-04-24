package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val mediaId: String,
    val albumId: String,
    val mediaGroup: MediaGroup,
    val trackIndex: Int
) : BaseContextMenuState(listItems, menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialTrackContextMenuStateProvider(savedStateHandle: SavedStateHandle): TrackContextMenuState {
        val mediaId = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val mediaGroupType =
            MediaGroupType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        val mediaGroupMediaId = savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_ID) ?: ""
        val trackIndex = savedStateHandle.get<Int>(NavArgs.TRACK_INDEX) ?: 0
        return TrackContextMenuState(
            mediaId = mediaId,
            menuTitle = "",
            albumId = "",
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(),
            trackIndex = trackIndex
        )
    }
}

@HiltViewModel
class TrackContextMenuViewModel @Inject constructor(
    initialState: TrackContextMenuState,
    trackRepository: TrackRepository,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<TrackContextMenuState>(initialState) {
    private var artistName = ""

    init {
        collect(trackRepository.getTrack(state.value.mediaId)) {
            if (it.artists.size == 1) {
                artistName = it.artists[0]
            }
            setState {
                copy(
                    menuTitle = it.track.trackName,
                    listItems = if (state.value.mediaGroup.mediaGroupType == MediaGroupType.ALBUM) {
                        listOf(
                            ContextMenuItem.Play,
                            ContextMenuItem.AddToQueue,
                            if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                        )
                    } else {
                        listOf(
                            ContextMenuItem.Play,
                            ContextMenuItem.AddToQueue,
                            if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                            ContextMenuItem.ViewAlbum
                        )
                    },
                    albumId = it.track.albumId,
                )
            }
        }
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.Play -> {
                with(state.value) {
                    viewModelScope.launch {
                        when (mediaGroup.mediaGroupType) {
                            MediaGroupType.ALL_TRACKS -> {
                                playbackManager.playAllTracks(trackIndex)
                            }
                            MediaGroupType.GENRE -> {
                                playbackManager.playGenre(
                                    genreName = mediaGroup.mediaId,
                                    initialTrackIndex = trackIndex
                                )
                            }
                            MediaGroupType.ALBUM -> {
                                playbackManager.playAlbum(
                                    albumId = mediaGroup.mediaId,
                                    initialTrackIndex = trackIndex
                                )
                            }
                            MediaGroupType.PLAYLIST -> {
                                playbackManager.playPlaylist(
                                    playlistName = mediaGroup.mediaId
                                )
                            }
                            MediaGroupType.SINGLE_TRACK -> playbackManager.playSingleTrack(mediaId)
                            MediaGroupType.ARTIST, MediaGroupType.UNKNOWN -> throw IllegalStateException(
                                "Unsupported media group type: ${mediaGroup.mediaGroupType}"
                            )
                        }
                        addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                    }
                }
            }
            ContextMenuItem.AddToQueue -> {
                viewModelScope.launch {
                    playbackManager.addToQueue(listOf(state.value.mediaId))
                }
            }
            ContextMenuItem.ViewAlbum -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToAlbum(state.value.albumId))
            }
            ContextMenuItem.ViewArtist -> {
                addUiEvent(
                    BaseContextMenuUiEvent.NavigateToArtist(artistName)
                )
            }
            ContextMenuItem.ViewArtists -> {
                addUiEvent(
                    BaseContextMenuUiEvent.NavigateToArtistsBottomSheet(
                        state.value.mediaId,
                        MediaType.TRACK
                    )
                )
            }
            else -> throw IllegalStateException("Invalid row for track context menu")
        }
    }
}