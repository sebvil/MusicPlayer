package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
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
import com.sebastianvm.musicplayer.ui.util.mvvm.launchViewModelIOScope
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.SortSettings
import com.sebastianvm.musicplayer.util.getStringComparator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
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
        val tracksListFlow =
            state.value.tracksListTitle?.let { trackRepository.getTracksForGenre(genreName = it) }
                ?: trackRepository.getAllTracks()

        collect(tracksListFlow.combine(preferencesRepository.getTracksListSortOptions(genreName = state.value.tracksListTitle)) { trackList, sortSettings ->
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
                launchViewModelIOScope {
                    val mediaGroup = MediaGroup(
                        mediaGroupType = state.value.tracksListTitle?.let { MediaGroupType.GENRE }
                            ?: MediaGroupType.ALL_TRACKS,
                        mediaId = state.value.tracksListTitle ?: ""
                    )
                    mediaQueueRepository.createQueue(
                        mediaGroup = mediaGroup,
                        sortOrder = state.value.sortOrder,
                        sortOption = state.value.currentSort
                    )
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
            is TracksListUserAction.SortOptionClicked -> {
                val sortOrder = if (action.newSortOption == state.value.currentSort) {
                    !state.value.sortOrder
                } else {
                    state.value.sortOrder
                }

                launchViewModelIOScope {
                    preferencesRepository.modifyTrackListSortOptions(
                        SortSettings(
                            sortOption = action.newSortOption,
                            sortOrder = sortOrder
                        ),
                        state.value.tracksListTitle
                    )
                    addUiEvent(TracksListUiEvent.ScrollToTop)
                }
            }
            is TracksListUserAction.TrackContextMenuClicked -> {
                addUiEvent(
                    TracksListUiEvent.OpenContextMenu(
                        action.trackId,
                        state.value.tracksListTitle,
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
    val tracksListTitle: String?,
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
            tracksListTitle = genreName,
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
