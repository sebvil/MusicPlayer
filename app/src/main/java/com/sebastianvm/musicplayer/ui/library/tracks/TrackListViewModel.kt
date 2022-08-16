package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrackListViewModel @Inject constructor(
    initialState: TrackListState,
    trackRepository: TrackRepository,
    genreRepository: GenreRepository,
    preferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<TrackListUiEvent, TrackListState>(
    initialState
) {

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

        val listNameFlow: Flow<String?> = when (state.value.trackListType) {
            TrackListType.ALL_TRACKS -> flow { emit(null) }
            TrackListType.GENRE -> genreRepository.getGenre(state.value.trackListId)
                .map { it.genreName }
        }

        preferencesRepository.getTrackListSortPreferences(
            trackListType = state.value.trackListType,
            trackListId = state.value.trackListId
        ).flatMapLatest {
            setState {
                copy(
                    sortPreferences = it
                )
            }
            combine(trackListFlow(it), listNameFlow) { tracks, listName ->
                Pair(tracks, listName)
            }
        }.onEach { (newTrackList, listName) ->
            setState {
                copy(
                    trackListName = listName,
                    trackList = newTrackList.map { it.toModelListItemState() },
                )
            }
            addUiEvent(TrackListUiEvent.ScrollToTop)
        }.launchIn(viewModelScope)

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
                        listType = SortableListType.Tracks(state.value.trackListType),
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
    val trackListName: String?,
    val trackListType: TrackListType,
    val trackList: List<ModelListItemState>,
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
            trackListName = null,
            trackList = listOf(),
            trackListType = args.trackListType,
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.TrackListSortOptions.TRACK),
        )
    }
}

sealed class TrackListUiEvent : UiEvent {
    object ScrollToTop : TrackListUiEvent()
}

