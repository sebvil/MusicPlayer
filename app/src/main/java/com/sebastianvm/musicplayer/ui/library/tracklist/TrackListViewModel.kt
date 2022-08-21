package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class TrackListViewModel @Inject constructor(
    initialState: TrackListState,
) : BaseViewModel<TrackListUiEvent, TrackListState>(initialState),
    ViewModelInterface<TrackListState, TrackListUserAction> {


    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
            is TrackListUserAction.SortByButtonClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Tracks(trackListType = TrackListType.ALL_TRACKS),
                                mediaId = ALL_TRACKS
                            )
                        )
                    )
                )
            }
        }
    }

    companion object {
        const val ALL_TRACKS = 0L
    }
}


object TrackListState : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackListStateModule {

    @Provides
    @ViewModelScoped
    fun initialTrackListStateProvider(savedStateHandle: SavedStateHandle): TrackListState {
        return TrackListState
    }
}

sealed interface TrackListUiEvent : UiEvent

sealed interface TrackListUserAction : UserAction {
    object UpButtonClicked : TrackListUserAction
    object SortByButtonClicked : TrackListUserAction
}