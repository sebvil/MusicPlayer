package com.sebastianvm.musicplayer.ui.library.tracklist

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
class AllTracksViewModel @Inject constructor(
    initialState: AllTracksState,
) : BaseViewModel<AllTracksUiEvent, AllTracksState>(initialState),
    ViewModelInterface<AllTracksState, AllTracksUserAction> {


    override fun handle(action: AllTracksUserAction) {
        when (action) {
            is AllTracksUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
            is AllTracksUserAction.SortByButtonClicked -> {
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


object AllTracksState : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAllTracksStateProviderModule {

    @Provides
    @ViewModelScoped
    fun initialAllTracksStateProvider(): AllTracksState {
        return AllTracksState
    }
}

sealed interface AllTracksUiEvent : UiEvent

sealed interface AllTracksUserAction : UserAction {
    object UpButtonClicked : AllTracksUserAction
    object SortByButtonClicked : AllTracksUserAction
}