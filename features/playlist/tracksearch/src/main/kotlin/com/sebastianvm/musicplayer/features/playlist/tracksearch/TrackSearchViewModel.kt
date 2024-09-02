package com.sebastianvm.musicplayer.features.playlist.tracksearch

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackSearchResult(val state: TrackRow.State, val inPlaylist: Boolean)

data class TrackSearchState(
    val trackSearchResults: List<TrackSearchResult>,
    val trackAddedToPlaylist: String? = null,
) : State

sealed interface TrackSearchUserAction : UserAction {
    data class TextChanged(val newText: String) : TrackSearchUserAction

    data class TrackClicked(val trackId: Long, val trackName: String) : TrackSearchUserAction

    data object ToastShown : TrackSearchUserAction

    data object BackClicked : TrackSearchUserAction
}

class TrackSearchViewModel(
    private val arguments: TrackSearchArguments,
    private val playlistRepository: PlaylistRepository,
    private val ftsRepository: FullTextSearchRepository,
    vmScope: CoroutineScope = getViewModelScope(),
    private val navController: NavController,
) : BaseViewModel<TrackSearchState, TrackSearchUserAction>(viewModelScope = vmScope) {

    private val query = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchResults =
        query.debounce(SEARCH_DEBOUNCE_TIME_MS).flatMapLatest { ftsRepository.searchTracks(it) }

    private val trackAddedToPlaylist = MutableStateFlow<String?>(null)

    override val state: StateFlow<TrackSearchState> =
        combine(
                searchResults,
                playlistRepository.getTrackIdsInPlaylist(arguments.playlistId),
                trackAddedToPlaylist,
            ) { tracks, playlistTrackIds, trackAddedToPlaylist ->
                val results =
                    tracks
                        .map {
                            TrackSearchResult(
                                TrackRow.State.fromTrack(it),
                                inPlaylist = it.id in playlistTrackIds,
                            )
                        }
                        .sortedBy { it.inPlaylist }
                TrackSearchState(
                    trackSearchResults = results,
                    trackAddedToPlaylist = trackAddedToPlaylist,
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                TrackSearchState(trackSearchResults = emptyList()),
            )

    override fun handle(action: TrackSearchUserAction) {
        when (action) {
            is TrackSearchUserAction.TextChanged -> {
                query.update { action.newText }
            }
            is TrackSearchUserAction.TrackClicked -> {
                viewModelScope.launch {
                    playlistRepository.addTrackToPlaylist(
                        playlistId = arguments.playlistId,
                        trackId = action.trackId,
                    )

                    trackAddedToPlaylist.update { action.trackName }
                }
            }
            is TrackSearchUserAction.ToastShown -> trackAddedToPlaylist.update { null }
            is TrackSearchUserAction.BackClicked -> navController.pop()
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_TIME_MS = 500L
    }
}
