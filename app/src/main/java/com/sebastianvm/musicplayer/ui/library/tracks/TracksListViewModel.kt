package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.getStringComparator
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
    preferencesRepository: SortPreferencesRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
    private val mediaQueueRepository: MediaQueueRepository,
) : BaseViewModel<TracksListUiEvent, TracksListState>(
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

        viewModelScope.launch {
            combine(
                tracksListFlow,
                preferencesRepository.getTracksListSortOptions(
                    tracksListType = state.value.tracksListType,
                    tracksListName = state.value.tracksListTitle
                )
            ) { trackList, sortSettings ->
                Pair(trackList, sortSettings)
            }.collect { (tracksList, sortSettings) ->
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
            addUiEvent(TracksListUiEvent.ScrollToTop)
        }

    }

    fun onTrackClicked(trackId: String) {
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
            mediaPlaybackRepository.playFromId(trackId, mediaGroup)
            addUiEvent(TracksListUiEvent.NavigateToPlayer)
        }
    }

    fun onSortByClicked() {
        addUiEvent(TracksListUiEvent.ShowSortBottomSheet)
    }

    fun onTrackOverflowMenuIconClicked(trackId: String) {
        val mediaGroup = MediaGroup(
            mediaGroupType = when (state.value.tracksListType) {
                TracksListType.ALL_TRACKS -> MediaGroupType.ALL_TRACKS
                TracksListType.GENRE -> MediaGroupType.GENRE
                TracksListType.PLAYLIST -> MediaGroupType.PLAYLIST
            },
            mediaId = state.value.tracksListTitle.ifEmpty { ALL_TRACKS }
        )
        addUiEvent(TracksListUiEvent.OpenContextMenu(trackId, mediaGroup))
    }

    fun onUpButtonClicked() {
        addUiEvent(TracksListUiEvent.NavigateUp)
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
        const val ALL_TRACKS = ""
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
        val listName = savedStateHandle[NavArgs.TRACK_LIST_NAME] ?: TracksListViewModel.ALL_TRACKS
        val listGroupType = savedStateHandle.get<String>(NavArgs.TRACKS_LIST_TYPE)!!
        return TracksListState(
            tracksListTitle = listName,
            tracksList = listOf(),
            tracksListType = TracksListType.valueOf(listGroupType),
            currentSort = MediaSortOption.TRACK,
            sortOrder = MediaSortOrder.ASCENDING,
        )
    }
}

sealed class TracksListUiEvent : UiEvent {
    object ScrollToTop : TracksListUiEvent()
    object NavigateToPlayer : TracksListUiEvent()
    object ShowSortBottomSheet : TracksListUiEvent()
    object NavigateUp : TracksListUiEvent()
    data class OpenContextMenu(val trackId: String, val mediaGroup: MediaGroup) :
        TracksListUiEvent()
}

