package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TracksListViewModel @Inject constructor(
    initialState: TracksListState,
    trackRepository: TrackRepository,
    preferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<TracksListUiEvent, TracksListState>(
    initialState
) {

    // TODO get genre name
    init {
        val tracksListFlow = when (state.value.tracksListType) {
            TracksListType.ALL_TRACKS -> { sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions> ->
                trackRepository.getAllTracks(
                    sortPreferences
                )
            }
            TracksListType.GENRE -> { sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions> ->
                trackRepository.getTracksForGenre(
                    genreId = state.value.tracksListId,
                    mediaSortPreferences = sortPreferences
                )
            }
        }

        viewModelScope.launch {
            preferencesRepository.getTracksListSortPreferences(
                tracksListType = state.value.tracksListType,
                tracksListId = state.value.tracksListId
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

    fun onTrackClicked(trackIndex: Int) {
        val playTracksFlow = when (state.value.tracksListType) {
            TracksListType.ALL_TRACKS -> {
                playbackManager.playAllTracks(initialTrackIndex = trackIndex)
            }
            TracksListType.GENRE -> {
                playbackManager.playGenre(
                    state.value.tracksListId,
                    initialTrackIndex = trackIndex,
                )
            }
        }
        playTracksFlow.onEach {
            when (it) {
                is PlaybackResult.Loading, is PlaybackResult.Error -> setState { copy(playbackResult = it) }
                is PlaybackResult.Success -> {
                    setState { copy(playbackResult = it) }
                    addUiEvent(TracksListUiEvent.NavigateToPlayer)
                }
            }
        }.launchIn(viewModelScope)


    }

    fun onSortByClicked() {
        addUiEvent(TracksListUiEvent.ShowSortBottomSheet(mediaId = state.value.tracksListId))
    }

    fun onTrackOverflowMenuIconClicked(trackIndex: Int, trackId: Long) {
        val mediaGroup = MediaGroup(
            mediaGroupType = when (state.value.tracksListType) {
                TracksListType.ALL_TRACKS -> MediaGroupType.ALL_TRACKS
                TracksListType.GENRE -> MediaGroupType.GENRE
            },
            mediaId = state.value.tracksListId
        )
        addUiEvent(TracksListUiEvent.OpenContextMenu(trackId, mediaGroup, trackIndex))
    }

    fun onUpButtonClicked() {
        addUiEvent(TracksListUiEvent.NavigateUp)
    }

    fun onClosePlaybackErrorDialog() {
        setState { copy(playbackResult = null) }
    }

    companion object {
        const val ALL_TRACKS = 0L
    }

}


data class TracksListState(
    val tracksListId: Long,
    val tracksListName: String,
    val tracksListType: TracksListType,
    val tracksList: List<TrackRowState>,
    val sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
    val playbackResult: PlaybackResult? = null
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTracksListStateModule {

    @Provides
    @ViewModelScoped
    fun initialTracksListStateProvider(savedStateHandle: SavedStateHandle): TracksListState {
        val listId: Long = savedStateHandle[NavArgs.TRACK_LIST_ID] ?: TracksListViewModel.ALL_TRACKS
        val listGroupType = savedStateHandle.get<String>(NavArgs.TRACKS_LIST_TYPE)!!
        return TracksListState(
            tracksListId = listId,
            tracksListName = "",
            tracksList = listOf(),
            tracksListType = TracksListType.valueOf(listGroupType),
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.TrackListSortOptions.TRACK),
        )
    }
}

sealed class TracksListUiEvent : UiEvent {
    object ScrollToTop : TracksListUiEvent()
    object NavigateToPlayer : TracksListUiEvent()
    data class ShowSortBottomSheet(val mediaId: Long) : TracksListUiEvent()
    object NavigateUp : TracksListUiEvent()
    data class OpenContextMenu(
        val trackId: Long,
        val mediaGroup: MediaGroup,
        val trackIndex: Int
    ) :
        TracksListUiEvent()
}

