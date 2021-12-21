package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.PERMISSION_GRANTED
import com.sebastianvm.musicplayer.PermissionStatus
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.SHOULD_REQUEST_PERMISSION
import com.sebastianvm.musicplayer.SHOULD_SHOW_EXPLANATION
import com.sebastianvm.musicplayer.repository.MusicRepository
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
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
) : BaseViewModel<LibraryUserAction, LibraryUiEvent, LibraryState>(initialState) {


    init {
        collect(musicRepository.getCounts()) { counts ->
            setState {
                copy(
                    libraryItems = libraryItems.map { item ->
                        when (item.rowId) {
                            NavRoutes.TRACKS_ROOT -> item.copy(count = counts.tracks)
                            NavRoutes.ARTISTS_ROOT -> item.copy(count = counts.artists)
                            NavRoutes.ALBUMS_ROOT -> item.copy(count = counts.albums)
                            NavRoutes.GENRES_ROOT -> item.copy(count = counts.genres)
                            else -> throw IllegalStateException("Illegal rowId ${item.rowId}")

                        }
                    }
                )
            }
        }
    }


    override fun handle(action: LibraryUserAction) {
        when (action) {
            is LibraryUserAction.FabClicked -> {
                when (action.permissionStatus) {
                    PERMISSION_GRANTED -> {
                        this.addUiEvent(LibraryUiEvent.StartGetMusicService)
                    }
                    SHOULD_SHOW_EXPLANATION -> {
                        setState {
                            copy(
                                showPermissionExplanationDialog = true
                            )
                        }
                    }
                    SHOULD_REQUEST_PERMISSION -> {
                        this.addUiEvent(LibraryUiEvent.RequestPermission)
                    }
                }
            }
            is LibraryUserAction.RowClicked -> {
                addUiEvent(LibraryUiEvent.NavigateToScreen(action.rowId))
            }
            is LibraryUserAction.PermissionGranted -> {
                this.addUiEvent(LibraryUiEvent.StartGetMusicService)
            }
            is LibraryUserAction.PermissionDenied -> {
                when (action.permissionStatus) {
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
            is LibraryUserAction.DismissPermissionDeniedDialog -> {
                setState {
                    copy(
                        showPermissionDeniedDialog = false
                    )
                }
            }
            is LibraryUserAction.PermissionDeniedConfirmButtonClicked -> {
                this.addUiEvent(LibraryUiEvent.OpenAppSettings)
            }
            is LibraryUserAction.DismissPermissionExplanationDialog -> {
                setState {
                    copy(
                        showPermissionExplanationDialog = false
                    )
                }
            }
            is LibraryUserAction.PermissionExplanationDialogContinueClicked -> {
                setState {
                    copy(
                        showPermissionExplanationDialog = false
                    )
                }
                this.addUiEvent(LibraryUiEvent.RequestPermission)
            }
        }
    }


}

data class LibraryState(
    val libraryItems: List<LibraryItem>,
    val showPermissionDeniedDialog: Boolean,
    val showPermissionExplanationDialog: Boolean,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialLibraryStateModule {

    @Provides
    @ViewModelScoped
    fun initialLibraryStateProvider() = LibraryState(
        libraryItems = listOf(
            LibraryItem(
                rowId = NavRoutes.TRACKS_ROOT,
                rowName = R.string.all_songs,
                icon = R.drawable.ic_song,
                countString = R.plurals.number_of_tracks,
                count = 0
            ),
            LibraryItem(
                rowId = NavRoutes.ARTISTS_ROOT,
                rowName = R.string.artists,
                icon = R.drawable.ic_artist,
                countString = R.plurals.number_of_artists,
                count = 0
            ),
            LibraryItem(
                rowId = NavRoutes.ALBUMS_ROOT,
                rowName = R.string.albums,
                icon = R.drawable.ic_album,
                countString = R.plurals.number_of_albums,
                count = 0
            ),
            LibraryItem(
                rowId = NavRoutes.GENRES_ROOT,
                rowName = R.string.genres,
                icon = R.drawable.ic_genre,
                countString = R.plurals.number_of_genres,
                count = 0
            ),
        ),
        showPermissionDeniedDialog = false,
        showPermissionExplanationDialog = false,
    )

}

sealed class LibraryUserAction : UserAction {
    data class FabClicked(@PermissionStatus val permissionStatus: String) : LibraryUserAction()
    data class RowClicked(val rowId: String) : LibraryUserAction()
    object PermissionGranted : LibraryUserAction()
    data class PermissionDenied(@PermissionStatus val permissionStatus: String) :
        LibraryUserAction()

    object DismissPermissionDeniedDialog : LibraryUserAction()
    object PermissionDeniedConfirmButtonClicked : LibraryUserAction()
    object DismissPermissionExplanationDialog : LibraryUserAction()
    object PermissionExplanationDialogContinueClicked : LibraryUserAction()
}

sealed class LibraryUiEvent : UiEvent {
    object StartGetMusicService : LibraryUiEvent()
    object RequestPermission : LibraryUiEvent()
    data class NavigateToScreen(val rowId: String) : LibraryUiEvent()
    object OpenAppSettings : LibraryUiEvent()
}
