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
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
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
            TracksListType.ALL_TRACKS -> { sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions> ->
                trackRepository.getAllTracks(
                    sortPreferences
                )
            }
            TracksListType.GENRE -> { sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions> ->
                trackRepository.getTracksForGenre(
                    genreName = state.value.tracksListTitle,
                    mediaSortPreferences = sortPreferences
                )
            }
        }

        viewModelScope.launch {
            preferencesRepository.getTracksListSortPreferences(
                tracksListType = state.value.tracksListType,
                tracksListName = state.value.tracksListTitle
            ).flatMapLatest {
                setState {
                    copy(
                        sortPreferences = it
                    )
                }
                tracksListFlow(it)
            }.collect { newTracksList ->
                setState {
                    copy(
                        tracksList = newTracksList.map { it.toTrackRowState(includeTrackNumber = false) },
                    )
                }
                addUiEvent(TracksListUiEvent.ScrollToTop)
            }
        }

    }

    fun onTrackClicked(trackId: String) {
        viewModelScope.launch {
            val mediaGroup = MediaGroup(
                mediaGroupType = when (state.value.tracksListType) {
                    TracksListType.ALL_TRACKS -> MediaGroupType.ALL_TRACKS
                    TracksListType.GENRE -> MediaGroupType.GENRE
                },
                mediaId = state.value.tracksListTitle
            )
            mediaQueueRepository.createQueue(mediaGroup = mediaGroup)
            mediaPlaybackRepository.playFromId(trackId, mediaGroup)
            addUiEvent(TracksListUiEvent.NavigateToPlayer)
        }
    }

    fun onSortByClicked() {
        addUiEvent(TracksListUiEvent.ShowSortBottomSheet(mediaId = state.value.tracksListTitle))
    }

    fun onTrackOverflowMenuIconClicked(trackId: String) {
        val mediaGroup = MediaGroup(
            mediaGroupType = when (state.value.tracksListType) {
                TracksListType.ALL_TRACKS -> MediaGroupType.ALL_TRACKS
                TracksListType.GENRE -> MediaGroupType.GENRE
            },
            mediaId = state.value.tracksListTitle.ifEmpty { ALL_TRACKS }
        )
        addUiEvent(TracksListUiEvent.OpenContextMenu(trackId, mediaGroup))
    }

    fun onUpButtonClicked() {
        addUiEvent(TracksListUiEvent.NavigateUp)
    }

    companion object {
        const val ALL_TRACKS = ""
    }

}


data class TracksListState(
    val tracksListTitle: String,
    val tracksListType: TracksListType,
    val tracksList: List<TrackRowState>,
    val sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTracksListStateModule {

    @Provides
    @ViewModelScoped
    fun initialTracksListStateProvider(savedStateHandle: SavedStateHandle): TracksListState {
        val listName =
            savedStateHandle[NavArgs.TRACK_LIST_NAME] ?: TracksListViewModel.ALL_TRACKS
        val listGroupType = savedStateHandle.get<String>(NavArgs.TRACKS_LIST_TYPE)!!
        return TracksListState(
            tracksListTitle = listName,
            tracksList = listOf(),
            tracksListType = TracksListType.valueOf(listGroupType),
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.TrackListSortOptions.TRACK)
        )
    }
}

sealed class TracksListUiEvent : UiEvent {
    object ScrollToTop : TracksListUiEvent()
    object NavigateToPlayer : TracksListUiEvent()
    data class ShowSortBottomSheet(val mediaId: String) : TracksListUiEvent()
    object NavigateUp : TracksListUiEvent()
    data class OpenContextMenu(val trackId: String, val mediaGroup: MediaGroup) :
        TracksListUiEvent()
}

