package com.sebastianvm.musicplayer.ui.library.tracks

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MEDIA_GROUP
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.PreferencesRepository
import com.sebastianvm.musicplayer.repository.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.getStringComparator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TracksListViewModel @Inject constructor(
    initialState: TracksListState,
    trackRepository: TrackRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val preferencesRepository: PreferencesRepository,
    private val mediaQueueRepository: MediaQueueRepository,
) : BaseViewModel<TracksListUserAction, TracksListUiEvent, TracksListState>(
    initialState
) {

    init {
        collect(preferencesRepository.getTracksListSortOptions(genreName = state.value.genreName)) { settings ->
            setState {
                copy(
                    currentSort = settings.sortOption,
                    tracksList = tracksList.sortedWith(
                        getComparator(
                            settings.sortOrder,
                            settings.sortOption
                        )
                    ),
                    sortOrder = settings.sortOrder
                )
            }

        }
        state.value.genreName?.also { genre ->
            collect(trackRepository.getTracksForGenre(genre)) { tracks ->
                setState {
                    copy(
                        tracksList = tracks.map { it.toTrackRowState() }.sortedWith(
                            getComparator(sortOrder, currentSort)
                        )
                    )
                }
            }
        } ?: kotlin.run {
            collect(trackRepository.getAllTracks()) { tracks ->
                setState {
                    copy(
                        tracksList = tracks.map { it.toTrackRowState() }.sortedWith(
                            getComparator(sortOrder, currentSort)
                        )
                    )
                }
            }
        }
    }

    override fun handle(action: TracksListUserAction) {
        when (action) {
            is TracksListUserAction.TrackClicked -> {
                val transportControls = musicServiceConnection.transportControls
                viewModelScope.launch {
                    val mediaGroup = MediaGroup(
                        mediaType = state.value.genreName?.let { MediaType.GENRE }
                            ?: MediaType.TRACK,
                        mediaId = state.value.genreName ?: ""
                    )
                    mediaQueueRepository.createQueue(
                        mediaGroup = mediaGroup,
                        sortOrder = state.value.sortOrder,
                        sortOption = state.value.currentSort
                    )
                    val extras = Bundle().apply {
                        putParcelable(MEDIA_GROUP, mediaGroup)
                    }
                    transportControls.playFromMediaId(action.trackId, extras)
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
            is TracksListUserAction.SortOptionClicked -> {
                val sortOrder = if (action.newSortOption == state.value.currentSort) {
                    !state.value.sortOrder
                } else {
                    state.value.sortOrder
                }

                viewModelScope.launch {
                    preferencesRepository.modifyTrackListSortOptions(
                        sortOrder,
                        action.newSortOption,
                        state.value.genreName
                    )
                    addUiEvent(TracksListUiEvent.ScrollToTop)
                }
            }
            is TracksListUserAction.TrackContextMenuClicked -> {
                addUiEvent(
                    TracksListUiEvent.OpenContextMenu(
                        action.trackId,
                        state.value.genreName,
                        state.value.currentSort,
                        state.value.sortOrder
                    )
                )
            }
            is TracksListUserAction.UpButtonClicked -> addUiEvent(TracksListUiEvent.NavigateUp)
        }
    }

    private fun getComparator(
        sortOrder: SortOrder,
        sortOption: SortOption
    ): Comparator<TrackRowState> {
        return getStringComparator(sortOrder) { trackRowState ->
            when (sortOption) {
                SortOption.TRACK_NAME -> trackRowState.trackName
                SortOption.ARTIST_NAME -> trackRowState.artists
                SortOption.ALBUM_NAME -> trackRowState.albumName
                else -> throw IllegalStateException("Unknown sort option for tracks list: $sortOption")
            }
        }
    }


}


data class TracksListState(
    val genreName: String?,
    val tracksListTitle: DisplayableString,
    val tracksList: List<TrackRowState>,
    val currentSort: SortOption,
    val sortOrder: SortOrder
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialTracksListStateModule {
    @Provides
    @ViewModelScoped
    fun initialTracksListStateProvider(savedStateHandle: SavedStateHandle): TracksListState {
        val genreName = savedStateHandle.get<String?>(NavArgs.GENRE_NAME)
        return TracksListState(
            genreName = genreName,
            tracksListTitle = genreName?.let { DisplayableString.StringValue(it) }
                ?: DisplayableString.ResourceValue(R.string.all_songs),
            tracksList = listOf(),
            currentSort = SortOption.TRACK_NAME,
            sortOrder = SortOrder.ASCENDING
        )
    }
}

sealed class TracksListUserAction : UserAction {
    data class TrackClicked(val trackId: String) : TracksListUserAction()
    object SortByClicked : TracksListUserAction()
    data class SortOptionClicked(val newSortOption: SortOption) : TracksListUserAction()
    data class TrackContextMenuClicked(val trackId: String) : TracksListUserAction()
    object UpButtonClicked : TracksListUserAction()
}

sealed class TracksListUiEvent : UiEvent {
    object NavigateToPlayer : TracksListUiEvent()
    data class ShowSortBottomSheet(@StringRes val sortOption: Int, val sortOrder: SortOrder) :
        TracksListUiEvent()

    object NavigateUp : TracksListUiEvent()

    data class OpenContextMenu(
        val trackId: String,
        val genreName: String?,
        val currentSort: SortOption,
        val sortOrder: SortOrder
    ) : TracksListUiEvent()

    object ScrollToTop : TracksListUiEvent()
}


