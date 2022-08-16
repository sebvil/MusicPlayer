package com.sebastianvm.musicplayer.ui.library.root

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.music.MusicRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    musicRepository: MusicRepository,
    initialState: LibraryState,
) : BaseViewModel<LibraryUiEvent, LibraryState>(initialState),
    ViewModelInterface<LibraryState, LibraryUserAction> {

    init {
        viewModelScope.launch {
            musicRepository.getCounts().collect { counts ->
                setState {
                    copy(libraryItems = libraryItems.map { item ->
                        when (item) {
                            is LibraryItem.Tracks -> item.copy(count = counts.tracks)
                            is LibraryItem.Artists -> item.copy(count = counts.artists)
                            is LibraryItem.Albums -> item.copy(count = counts.albums)
                            is LibraryItem.Genres -> item.copy(count = counts.genres)
                            is LibraryItem.Playlists -> item.copy(count = counts.playlists)
                        }
                    })
                }

            }
        }
    }

    override fun handle(action: LibraryUserAction) {
        when (action) {
            is LibraryUserAction.RowClicked -> {
                addNavEvent(NavEvent.NavigateToScreen(action.destination))
            }
            is LibraryUserAction.SearchBoxClicked -> {
                addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.Search))
            }
        }
    }
}

data class LibraryState(val libraryItems: List<LibraryItem>) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialLibraryStateModule {

    @Provides
    @ViewModelScoped
    fun initialLibraryStateProvider() = LibraryState(
        libraryItems = listOf(
            LibraryItem.Tracks(count = 0),
            LibraryItem.Artists(count = 0),
            LibraryItem.Albums(count = 0),
            LibraryItem.Genres(count = 0),
            LibraryItem.Playlists(count = 0)
        )
    )
}

sealed class LibraryUiEvent : UiEvent {
    object RequestPermission : LibraryUiEvent()
}

sealed class LibraryUserAction : UserAction {
    data class RowClicked(val destination: NavigationDestination) : LibraryUserAction()
    object SearchBoxClicked : LibraryUserAction()
}
