package com.sebastianvm.musicplayer.features.playlist.tracksearch

import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
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

data class TrackSearchArguments(val playlistId: Long) : Arguments

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

class TrackSearchStateHolder(
    private val arguments: TrackSearchArguments,
    private val playlistRepository: PlaylistRepository,
    private val ftsRepository: FullTextSearchRepository,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val navController: NavController,
) : StateHolder<TrackSearchState, TrackSearchUserAction> {

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
                stateHolderScope,
                SharingStarted.Lazily,
                TrackSearchState(trackSearchResults = emptyList()),
            )

    override fun handle(action: TrackSearchUserAction) {
        when (action) {
            is TrackSearchUserAction.TextChanged -> {
                query.update { action.newText }
            }
            is TrackSearchUserAction.TrackClicked -> {
                stateHolderScope.launch {
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
