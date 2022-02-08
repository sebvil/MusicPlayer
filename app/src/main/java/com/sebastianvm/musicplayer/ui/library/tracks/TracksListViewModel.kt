package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.getStringComparator
import com.sebastianvm.musicplayer.util.sort.id
import com.sebastianvm.musicplayer.util.sort.mediaSortSettings
import com.sebastianvm.musicplayer.util.sort.not
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TracksListViewModel @Inject constructor(
    initialState: TracksListState,
    trackRepository: TrackRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
    private val preferencesRepository: PreferencesRepository,
    private val mediaQueueRepository: MediaQueueRepository,
) : BaseViewModel<TracksListUserAction, TracksListUiEvent, TracksListState>(
    initialState
) {

    init {
        val tracksListFlow = when (state.value.tracksListType) {
            TracksListType.ALL_TRACKS -> trackRepository.getAllTracks()
            TracksListType.GENRE -> trackRepository.getTracksForGenre(
                genreName = state.value.tracksListTitle
            )
            TracksListType.PLAYLIST -> trackRepository.getTracksForPlaylist(
                playlistName = state.value.tracksListTitle
            )
        }

        collect(
            tracksListFlow.combine(
                preferencesRepository.getTracksListSortOptions(
                    tracksListType = state.value.tracksListType,
                    tracksListName = state.value.tracksListTitle
                )
            ) { trackList, sortSettings ->
                Pair(trackList, sortSettings)
            }) { (tracksList, sortSettings) ->
            setState {
                copy(
                    currentSort = sortSettings.sortOption,
                    tracksList = tracksList.map { it.toTrackRowState(includeTrackNumber = false) }
                        .sortedWith(
                            getComparator(
                                sortSettings.sortOrder,
                                sortSettings.sortOption
                            )
                        ),
                    sortOrder = sortSettings.sortOrder
                )
            }
        }
    }

    override fun handle(action: TracksListUserAction) {
        when (action) {
            is TracksListUserAction.TrackClicked -> {
                viewModelScope.launch {
                    val mediaGroup = MediaGroup(
                        mediaGroupType = when (state.value.tracksListType) {
                            TracksListType.ALL_TRACKS -> MediaGroupType.ALL_TRACKS
                            TracksListType.GENRE -> MediaGroupType.GENRE
                            TracksListType.PLAYLIST -> MediaGroupType.PLAYLIST
                        },
                        mediaId = state.value.tracksListTitle
                    )
                    mediaQueueRepository.createQueue(mediaGroup = mediaGroup)
                    mediaPlaybackRepository.playFromId(action.trackId, mediaGroup)
                    addUiEvent(TracksListUiEvent.NavigateToPlayer)
                }
            }
            is TracksListUserAction.SortByClicked -> {
                addUiEvent(
                    TracksListUiEvent.ShowSortBottomSheet(
                        state.value.currentSort.id,
                        state.value.sortOrder
                    )
                )
            }
            is TracksListUserAction.MediaSortOptionClicked -> {
                val sortOrder = if (action.newSortOption == state.value.currentSort) {
                    !state.value.sortOrder
                } else {
                    state.value.sortOrder
                }

                viewModelScope.launch {
                    preferencesRepository.modifyTrackListSortOptions(
                        mediaSortSettings = mediaSortSettings {
                            sortOption = action.newSortOption
                            this.sortOrder = sortOrder
                        },
                        tracksListType = state.value.tracksListType,
                        state.value.tracksListTitle
                    )
                    addUiEvent(TracksListUiEvent.ScrollToTop)
                }
            }
            is TracksListUserAction.TrackContextMenuClicked -> {
                val mediaGroup = MediaGroup(
                    mediaGroupType = when (state.value.tracksListType) {
                        TracksListType.ALL_TRACKS -> MediaGroupType.ALL_TRACKS
                        TracksListType.GENRE -> MediaGroupType.GENRE
                        TracksListType.PLAYLIST -> MediaGroupType.PLAYLIST
                    },
                    mediaId = state.value.tracksListTitle.ifEmpty { ALL_TRACKS }
                )
                addUiEvent(
                    TracksListUiEvent.OpenContextMenu(
                        action.trackId,
                        mediaGroup
                    )
                )
            }
            is TracksListUserAction.UpButtonClicked -> addUiEvent(TracksListUiEvent.NavigateUp)
        }
    }

    private fun getComparator(
        sortOrder: MediaSortOrder,
        sortOption: MediaSortOption
    ): Comparator<TrackRowState> {
        return getStringComparator(sortOrder) { trackRowState ->
            when (sortOption) {
                MediaSortOption.TRACK -> trackRowState.trackName
                MediaSortOption.ARTIST -> trackRowState.artists
                MediaSortOption.ALBUM -> trackRowState.albumName
                else -> throw IllegalStateException("Unknown sort option for tracks list: $sortOption")
            }
        }
    }

    companion object {
        private const val ALL_TRACKS = ""
    }
}


data class TracksListState(
    val tracksListTitle: String,
    val tracksListType: TracksListType,
    val tracksList: List<TrackRowState>,
    val currentSort: MediaSortOption,
    val sortOrder: MediaSortOrder
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialTracksListStateModule {

    @Provides
    @ViewModelScoped
    fun initialTracksListStateProvider(savedStateHandle: SavedStateHandle): TracksListState {
        val listName = savedStateHandle.get<String?>(NavArgs.TRACK_LIST_NAME) ?: ""
        val listGroupType = savedStateHandle.get<String>(NavArgs.TRACKS_LIST_TYPE)!!
        return TracksListState(
            tracksListTitle = listName,
            tracksList = listOf(),
            tracksListType = TracksListType.valueOf(listGroupType),
            currentSort = MediaSortOption.TRACK,
            sortOrder = MediaSortOrder.ASCENDING
        )
    }
}

sealed class TracksListUserAction : UserAction {
    data class TrackClicked(val trackId: String) : TracksListUserAction()
    object SortByClicked : TracksListUserAction()
    data class MediaSortOptionClicked(val newSortOption: MediaSortOption) : TracksListUserAction()
    data class TrackContextMenuClicked(val trackId: String) : TracksListUserAction()
    object UpButtonClicked : TracksListUserAction()
}

sealed class TracksListUiEvent : UiEvent {
    object NavigateToPlayer : TracksListUiEvent()
    data class ShowSortBottomSheet(@StringRes val sortOption: Int, val sortOrder: MediaSortOrder) :
        TracksListUiEvent()

    object NavigateUp : TracksListUiEvent()

    data class OpenContextMenu(val trackId: String, val mediaGroup: MediaGroup) : TracksListUiEvent()

    object ScrollToTop : TracksListUiEvent()
}

