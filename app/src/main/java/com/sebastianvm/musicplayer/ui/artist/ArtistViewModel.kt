package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.AlbumType
import com.sebastianvm.musicplayer.util.extensions.getArgs
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
class ArtistViewModel @Inject constructor(
    initialState: ArtistState,
    artistRepository: ArtistRepository,
) : BaseViewModel<ArtistUiEvent, ArtistState>(initialState),
    ViewModelInterface<ArtistState, ArtistUserAction> {
    init {
        artistRepository.getArtist(state.value.artistId).onEach { artistWithAlbums ->
            setState {
                copy(
                    artistName = artistWithAlbums.artist.artistName,
                    albumsForArtistItems = artistWithAlbums.artistAlbums.takeUnless { it.isEmpty() }
                        ?.let { albums ->
                            listOf(
                                ArtistScreenItem.SectionHeaderItem(
                                    AlbumType.ALBUM,
                                    R.string.albums
                                )
                            ).plus(albums.sortedByDescending { album -> album.year }
                                .map { it.toAlbumRowItem() })

                        },
                    appearsOnForArtistItems = artistWithAlbums.artistAppearsOn.takeUnless { it.isEmpty() }
                        ?.let { albums ->
                            listOf(
                                ArtistScreenItem.SectionHeaderItem(
                                    AlbumType.APPEARS_ON,
                                    R.string.appears_on
                                )
                            ).plus(albums.sortedByDescending { album -> album.year }
                                .map { it.toAlbumRowItem() })
                        },
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: ArtistUserAction) {
        when (action) {
            is ArtistUserAction.AlbumClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Album(
                            AlbumArguments(
                                albumId = action.albumId
                            )
                        )
                    )
                )
            }
            is ArtistUserAction.AlbumOverflowMenuIconClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.AlbumContextMenu(
                            AlbumContextMenuArguments(albumId = action.albumId)
                        )
                    )
                )
            }
            is ArtistUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)

        }
    }

    private fun Album.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
        return ArtistScreenItem.AlbumRowItem(this.toModelListItemState())
    }

}

data class ArtistState(
    val artistId: Long,
    val artistName: String,
    val albumsForArtistItems: List<ArtistScreenItem>?,
    val appearsOnForArtistItems: List<ArtistScreenItem>?
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistState {
    @Provides
    @ViewModelScoped
    fun provideInitialArtistState(savedStateHandle: SavedStateHandle): ArtistState {
        val args = savedStateHandle.getArgs<ArtistArguments>()
        return ArtistState(
            artistId = args.artistId,
            artistName = "",
            albumsForArtistItems = null,
            appearsOnForArtistItems = null,
        )
    }
}

sealed class ArtistUiEvent : UiEvent {
    data class NavEvent(val navigationDestination: NavigationDestination) : ArtistUiEvent()
}

sealed interface ArtistUserAction : UserAction {
    data class AlbumClicked(val albumId: Long) : ArtistUserAction
    data class AlbumOverflowMenuIconClicked(val albumId: Long) : ArtistUserAction
    object UpButtonClicked : ArtistUserAction
}