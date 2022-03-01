package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.PERMISSION_GRANTED
import com.sebastianvm.musicplayer.PermissionStatus
import com.sebastianvm.musicplayer.SHOULD_REQUEST_PERMISSION
import com.sebastianvm.musicplayer.SHOULD_SHOW_EXPLANATION
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    musicRepository: MusicRepository,
    initialState: LibraryState
) : BaseViewModel<LibraryUiEvent, LibraryState>(initialState) {


    init {
        collect(musicRepository.getCounts()) { counts ->
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

    fun onFabClicked(@PermissionStatus permissionStatus: String) {
        when (permissionStatus) {
            PERMISSION_GRANTED -> {
                addUiEvent(LibraryUiEvent.StartGetMusicService)
            }
            SHOULD_SHOW_EXPLANATION -> {
                setState {
                    copy(
                        showPermissionExplanationDialog = true
                    )
                }
            }
            SHOULD_REQUEST_PERMISSION -> {
                addUiEvent(LibraryUiEvent.RequestPermission)
            }
        }
    }

    fun onRowClicked(rowId: String) {
        addUiEvent(LibraryUiEvent.NavigateToScreen(rowId))
    }

    fun onPermissionGranted() {
        addUiEvent(LibraryUiEvent.StartGetMusicService)
    }

    fun onPermissionDenied(@PermissionStatus permissionStatus: String) {
        when (permissionStatus) {
            SHOULD_SHOW_EXPLANATION -> {
                setState {
                    copy(
                        showPermissionExplanationDialog = true
                    )
                }
            }
            else -> {
                setState {
                    copy(
                        showPermissionDeniedDialog = true
                    )
                }
            }
        }
    }

    fun onDismissPermissionDeniedDialog() {
        setState { copy(showPermissionExplanationDialog = false) }
    }

    fun onPermissionDeniedConfirmButtonClicked() {
        addUiEvent(LibraryUiEvent.OpenAppSettings)
    }

    fun onDismissPermissionExplanationDialog() {
        setState { copy(showPermissionExplanationDialog = false) }
    }

    fun onPermissionExplanationDialogContinueClicked() {
        setState { copy(showPermissionExplanationDialog = false) }
        addUiEvent(LibraryUiEvent.RequestPermission)
    }

}

data class LibraryState(
    val libraryItems: List<LibraryItem>,
    val showPermissionDeniedDialog: Boolean,
    val showPermissionExplanationDialog: Boolean,
    override val events: List<LibraryUiEvent>
) : State<LibraryUiEvent> {

    @Suppress("UNCHECKED_CAST")
    override fun <S : State<LibraryUiEvent>> setEvent(events: List<LibraryUiEvent>): S {
        return copy(events = events) as S
    }
}

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
        ),
        showPermissionDeniedDialog = false,
        showPermissionExplanationDialog = false,
        events = listOf()
    )

}

sealed class LibraryUiEvent : UiEvent {
    object StartGetMusicService : LibraryUiEvent()
    object RequestPermission : LibraryUiEvent()
    data class NavigateToScreen(val rowId: String) : LibraryUiEvent()
    object OpenAppSettings : LibraryUiEvent()
}
