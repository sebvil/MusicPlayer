package com.sebastianvm.musicplayer.ui.library.root

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.*
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.extensions.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection,
    initialState: LibraryState
) : BaseViewModel<LibraryUserAction, LibraryUiEvent, LibraryState>(initialState) {


    init {
        musicServiceConnection.subscribe(
            BrowseTree.MEDIA_ROOT,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    setState {
                        copy(
                            libraryItems = children.mapNotNull { child ->
                                child.description.toLibraryItem()
                            },
                        )
                    }

                }
            }
        )
    }

    fun MediaDescriptionCompat.toLibraryItem(): LibraryItem? {
        val meta =
            extras?.getParcelable<MediaMetadataCompat>(MEDIA_METADATA_COMPAT_KEY) ?: return null
        val id = meta.id ?: return null
        val rowName = meta.title?.toIntOrNull() ?: return null
        val icon = meta.iconRes
        val count = meta.counts
        return LibraryItem(id, rowName, getCountStrings(rowName), icon, count)
    }

    @PluralsRes
    fun getCountStrings(@StringRes rowName: Int): Int {
        return when (rowName) {
            R.string.all_songs -> R.plurals.number_of_tracks
            R.string.artists -> R.plurals.number_of_artists
            R.string.albums -> R.plurals.number_of_albums
            R.string.genres -> R.plurals.number_of_genres
            else -> -1
        }
    }

    override fun handle(action: LibraryUserAction) {
        when (action) {
            is LibraryUserAction.FabClicked -> {
                when (action.permissionStatus) {
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
            is LibraryUserAction.RowClicked -> {
                addBlockingEvent(LibraryUiEvent.NavigateToScreen(action.rowGid))
            }
            is LibraryUserAction.PermissionGranted -> {
                addUiEvent(LibraryUiEvent.StartGetMusicService)
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
                addUiEvent(LibraryUiEvent.OpenAppSettings)
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
                addUiEvent(LibraryUiEvent.RequestPermission)
            }
        }
    }


}

data class LibraryState(
    val libraryItems: List<LibraryItem>,
    val showPermissionDeniedDialog: Boolean,
    val showPermissionExplanationDialog: Boolean,
    val isLoading: Boolean
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialLibraryStateModule {

    @Provides
    @ViewModelScoped
    fun initialLibraryStateProvider() = LibraryState(
        libraryItems = listOf(),
        showPermissionDeniedDialog = false,
        showPermissionExplanationDialog = false,
        isLoading = true
    )

}

sealed class LibraryUserAction : UserAction {
    data class FabClicked(@PermissionStatus val permissionStatus: String) : LibraryUserAction()
    data class RowClicked(val rowGid: String) : LibraryUserAction()
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
    data class NavigateToScreen(val rowGid: String) : LibraryUiEvent()
    object OpenAppSettings : LibraryUiEvent()
}
