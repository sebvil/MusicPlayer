package com.sebastianvm.musicplayer.ui.playlist

import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class TrackSearchViewModel @Inject constructor(
    initialState: TrackSearchState,
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
) :
    BaseViewModel<TrackSearchUiEvent, TrackSearchState>(initialState) {

    private val query = MutableStateFlow("")
    fun onTextChanged(newText: String) {
        query.update { newText }
    }
}

data class TrackSearchState(
    val trackSearchResults: List<TrackRowState>,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialTrackSearchStateProvider(): TrackSearchState {
        return TrackSearchState(
            trackSearchResults = listOf(),
        )
    }
}

sealed class TrackSearchUiEvent : UiEvent