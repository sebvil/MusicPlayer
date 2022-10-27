package com.sebastianvm.musicplayer.ui.library.root

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryItem
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    musicRepository: MusicRepository,
    initialState: LibraryState,
) : BaseViewModel<LibraryState, LibraryUserAction, LibraryUiEvent>(initialState) {

    init {
        musicRepository.getCounts().onEach { counts ->
            setState {
                copy(
                    tracksItem = LibraryItem.Tracks(count = counts.tracks),
                    artistsItem = LibraryItem.Artists(count = counts.artists),
                    albumsItem = LibraryItem.Albums(count = counts.albums),
                    genresItem = LibraryItem.Genres(count = counts.genres),
                    playlistsItem = LibraryItem.Playlists(count = counts.playlists),
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: LibraryUserAction) = Unit
}


data class LibraryState(
    val tracksItem: LibraryItem.Tracks,
    val artistsItem: LibraryItem.Artists,
    val albumsItem: LibraryItem.Albums,
    val genresItem: LibraryItem.Genres,
    val playlistsItem: LibraryItem.Playlists
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialLibraryStateModule {
    @Provides
    @ViewModelScoped
    fun initialLibraryStateProvider() = LibraryState(
        tracksItem = LibraryItem.Tracks(count = 0),
        artistsItem = LibraryItem.Artists(count = 0),
        albumsItem = LibraryItem.Albums(count = 0),
        genresItem = LibraryItem.Genres(count = 0),
        playlistsItem = LibraryItem.Playlists(count = 0)
    )
}

sealed class LibraryUiEvent : UiEvent
sealed class LibraryUserAction : UserAction