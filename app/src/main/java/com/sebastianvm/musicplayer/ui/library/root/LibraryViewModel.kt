package com.sebastianvm.musicplayer.ui.library.root

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
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
) : BaseViewModel<LibraryUiEvent, LibraryState>(initialState) {

    init {
        viewModelScope.launch {
            musicRepository.getCounts().collect { counts ->
                setState {
                    copy(
                        libraryItems = libraryItems.map { item ->
                            when (item) {
                                is LibraryItem.Tracks -> item.copy(count = counts.tracks)
                                is LibraryItem.Artists -> item.copy(count = counts.artists)
                                is LibraryItem.Albums -> item.copy(count = counts.albums)
                                is LibraryItem.Genres -> item.copy(count = counts.genres)
                                is LibraryItem.Playlists -> item.copy(count = counts.playlists)
                            }
                        }
                    )
                }

            }
        }
    }

    fun onRowClicked(rowId: String) {
        addUiEvent(LibraryUiEvent.NavigateToScreen(rowId))
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
    object StartGetMusicService : LibraryUiEvent()
    object RequestPermission : LibraryUiEvent()
    data class NavigateToScreen(val rowId: String) : LibraryUiEvent()
    data class NavEvent(val destination: NavigationDestination) : LibraryUiEvent()
}
