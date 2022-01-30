package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


data class TrackContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val mediaId: String,
    val albumId: String,
    val artistName: String,
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
            albumId = "",
            artistName = "",
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(),
            selectedSort = SortOption.valueOf(selectedSort),
            sortOrder = SortOrder.valueOf(sortOrder)
        )
    }
}

@HiltViewModel
class TrackContextMenuViewModel @Inject constructor(
    initialState: TrackContextMenuState,
    private val trackRepository: TrackRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
    private val preferencesRepository: PreferencesRepository,
) : BaseContextMenuViewModel<TrackContextMenuState>(initialState) {
    init {
        collect(trackRepository.getTrack(state.value.mediaId)) {
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
                    albumId = it.album.albumId,
                    artistName = if (it.artists.size == 1) it.artists[0].artistName else ""
                )
            }
        }
    }

    override fun handle(action: BaseContextMenuUserAction) {
        when (action) {
            is BaseContextMenuUserAction.RowClicked -> {
                when (action.row) {
                    is ContextMenuItem.Play -> {
                        with(state.value) {
                            viewModelScope.launch {
                                mediaQueueRepository.createQueue(
                                    mediaGroup = mediaGroup,
                                    sortOrder = sortOrder,
                                    sortOption = selectedSort
                                )
                                mediaPlaybackRepository.playFromId(mediaId, mediaGroup)
                                addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                            }
                        }

                    }
                    // TODO simiplify logic here
                    ContextMenuItem.AddToQueue -> {
                        viewModelScope.launch {
                            withContext(Dispatchers.IO) {
                                preferencesRepository.getSavedPlaybackInfo().first().also {
                                    if (it.currentQueue.mediaGroupType == MediaGroupType.UNKNOWN) {
                                        addUiEvent(
                                            BaseContextMenuUiEvent.ShowToast(
                                                R.string.no_queue_available,
                                                success = false
                                            )
                                        )
                                    } else {
                                        val tracks =
                                            trackRepository.getTracksForQueue(it.currentQueue)
                                                .first()
                                        val currentIndex =
                                            mediaPlaybackRepository.addToQueue(mediaId = state.value.mediaId)
                                        val oldQueue = tracks.mapIndexed { index, track ->
                                            MediaQueueTrackCrossRef(
                                                mediaGroupType = it.currentQueue.mediaGroupType,
                                                groupMediaId = it.currentQueue.mediaId,
                                                trackId = track.track.trackId,
                                                trackIndex = index
                                            )
                                        }
                                        val newQueue = oldQueue.map { queueItem ->
                                            queueItem.copy(
                                                trackIndex = if (queueItem.trackIndex < currentIndex) queueItem.trackIndex else queueItem.trackIndex + 1
                                            )
                                        }.toMutableList()
                                        newQueue.add(
                                            currentIndex, MediaQueueTrackCrossRef(
                                                mediaGroupType = it.currentQueue.mediaGroupType,
                                                groupMediaId = it.currentQueue.mediaId,
                                                trackId = state.value.mediaId,
                                                trackIndex = currentIndex
                                            )
                                        )
                                        mediaQueueRepository.insertOrUpdateMediaQueueTrackCrossRefs(
                                            it.currentQueue,
                                            newQueue
                                        )
                                        addUiEvent(
                                            BaseContextMenuUiEvent.ShowToast(
                                                R.string.added_to_queue,
                                                success = true
                                            )
                                        )

                                    }

                                }
                            }
                        }
                    }
                    ContextMenuItem.ViewAlbum -> {
                        addUiEvent(BaseContextMenuUiEvent.NavigateToAlbum(state.value.albumId))
                    }
                    ContextMenuItem.ViewArtist -> {
                        addUiEvent(
                            BaseContextMenuUiEvent.NavigateToArtist(
                                state.value.artistName
                            )
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
    }
}