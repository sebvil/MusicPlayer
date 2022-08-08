package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
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
class TrackListViewModel @Inject constructor(
    initialState: TrackListState,
    trackRepository: TrackRepository,
    preferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<TrackListUiEvent, TrackListState>(
    initialState
) {

    // TODO get genre name
    init {
        val trackListFlow = when (state.value.trackListType) {
            TrackListType.ALL_TRACKS -> { sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions> ->
                trackRepository.getAllTracks(
                    sortPreferences
                )
            }
            TrackListType.GENRE -> { sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions> ->
                trackRepository.getTracksForGenre(
                    genreId = state.value.trackListId,
                    mediaSortPreferences = sortPreferences
                )
            }
        }

        viewModelScope.launch {
            preferencesRepository.getTrackListSortPreferences(
                trackListType = state.value.trackListType,
                trackListId = state.value.trackListId
            ).flatMapLatest {
                setState {
                    copy(
                        sortPreferences = it
                    )
                }
                trackListFlow(it)
            }.collect { newTrackList ->
                setState {
                    copy(
                        trackList = newTrackList.map { it.toTrackRowState(includeTrackNumber = false) },
                    )
                }
                addUiEvent(TrackListUiEvent.ScrollToTop)
            }
        }

    }

    fun onTrackClicked(trackIndex: Int) {
        val playTracksFlow = when (state.value.trackListType) {
            TrackListType.ALL_TRACKS -> {
                playbackManager.playAllTracks(initialTrackIndex = trackIndex)
            }
            TrackListType.GENRE -> {
                playbackManager.playGenre(
                    state.value.trackListId,
                    initialTrackIndex = trackIndex,
                )
            }
        }
        playTracksFlow.onEach {
            when (it) {
                is PlaybackResult.Loading, is PlaybackResult.Error -> setState { copy(playbackResult = it) }
                is PlaybackResult.Success -> {
                    setState { copy(playbackResult = it) }
                    addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer))
                }
            }
        }.launchIn(viewModelScope)


    }

    fun onSortByClicked() {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.SortMenu(
                    SortMenuArguments(
                        SortableListType.TRACKS,
                        mediaId = state.value.trackListId
                    )
                )
            )
        )
    }

    fun onTrackOverflowMenuIconClicked(trackIndex: Int, trackId: Long) {
        val mediaGroup = MediaGroup(
            mediaGroupType = when (state.value.trackListType) {
                TrackListType.ALL_TRACKS -> MediaGroupType.ALL_TRACKS
                TrackListType.GENRE -> MediaGroupType.GENRE
            },
            mediaId = state.value.trackListId
        )
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackContextMenu(
                    TrackContextMenuArguments(
                        trackId = trackId,
                        mediaType = MediaType.TRACK,
                        mediaGroup = mediaGroup,
                        trackIndex = trackIndex
                    )
                )
            )
        )
    }

    fun onUpButtonClicked() {
        addNavEvent(NavEvent.NavigateUp)
    }

    fun onClosePlaybackErrorDialog() {
        setState { copy(playbackResult = null) }
    }

    companion object {
        const val ALL_TRACKS = 0L
    }

}


data class TrackListState(
    val trackListId: Long,
    val trackListName: String,
    val trackListType: TrackListType,
    val trackList: List<TrackRowState>,
    val sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
    val playbackResult: PlaybackResult? = null
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackListStateModule {

    @Provides
    @ViewModelScoped
    fun initialTrackListStateProvider(savedStateHandle: SavedStateHandle): TrackListState {
        val args = savedStateHandle.getArgs<TrackListArguments>()
        return TrackListState(
            trackListId = args.trackListId,
            trackListName = "",
            trackList = listOf(),
            trackListType = args.trackListType,
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.TrackListSortOptions.TRACK),
        )
    }
}

sealed class TrackListUiEvent : UiEvent {
    object ScrollToTop : TrackListUiEvent()
}

