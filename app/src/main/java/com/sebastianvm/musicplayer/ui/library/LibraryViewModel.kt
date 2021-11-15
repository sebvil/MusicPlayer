package com.sebastianvm.musicplayer.ui.library

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.MusicRepository
import com.sebastianvm.musicplayer.ui.library.LibraryUserAction.*
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.extensions.*
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
    private val musicRepository: MusicRepository,
    musicServiceConnection: MusicServiceConnection,
    initialState: LibraryState
) : BaseViewModel<LibraryUserAction, LibraryState>(initialState) {


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
                            isLoading = false
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
            is GetMusic -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                viewModelScope.launch {
                    musicRepository.getMusic()
                    musicRepository.updateCounts()
                }
            }
            is ShowPermissionDeniedDialog -> {
                setState {
                    copy(
                        showPermissionDeniedDialog = true
                    )
                }
            }
            is DismissPermissionDeniedDialog -> {
                setState {
                    copy(
                        showPermissionDeniedDialog = false
                    )
                }
            }
            is ShowPermissionExplanationDialog -> {
                setState {
                    copy(
                        showPermissionExplanationDialog = true
                    )
                }
            }
            is DismissPermissionExplanationDialog -> {
                setState {
                    copy(
                        showPermissionExplanationDialog = false
                    )
                }
            }
            is DismissProgressDialog -> {
                setState {
                    copy(
                        isLoading = false
                    )
                }
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
    object GetMusic : LibraryUserAction()
    object ShowPermissionDeniedDialog : LibraryUserAction()
    object DismissPermissionDeniedDialog : LibraryUserAction()
    object ShowPermissionExplanationDialog : LibraryUserAction()
    object DismissPermissionExplanationDialog : LibraryUserAction()
    object DismissProgressDialog : LibraryUserAction()
}
