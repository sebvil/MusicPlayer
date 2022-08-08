package com.sebastianvm.musicplayer.ui.playlist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrackSearchViewModel @Inject constructor(
    initialState: TrackSearchState,
    private val ftsRepository: FullTextSearchRepository,
) :
    BaseViewModel<TrackSearchUiEvent, TrackSearchState>(initialState) {

    private val query = MutableStateFlow("")

    init {
        setState {
            copy(
                trackSearchResults = query.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchTracksPaged(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.track.toTrackRowState(includeTrackNumber = false) }
                    }
                },
            )
        }
    }

    fun onTextChanged(newText: String) {
        query.update { newText }
    }
}

data class TrackSearchState(
    val trackSearchResults: Flow<PagingData<TrackRowState>>,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialTrackSearchStateProvider(): TrackSearchState {
        return TrackSearchState(
            trackSearchResults = emptyFlow(),
        )
    }
}

sealed class TrackSearchUiEvent : UiEvent