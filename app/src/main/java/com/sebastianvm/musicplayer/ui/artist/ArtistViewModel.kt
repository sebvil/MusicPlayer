package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    initialState: ArtistState,
    artistRepository: ArtistRepository,
) : BaseViewModel<ArtistState, ArtistUserAction, ArtistUiEvent>(initialState) {
    init {
        viewModelScope.launch {
            val artistWithAlbums = artistRepository.getArtist(state.artistId).first()
            setState {
                copy(
                    artistName = artistWithAlbums.artist.artistName,
                    listItems = buildList {
                        if (artistWithAlbums.artistAlbums.isNotEmpty()) {
                            add(ArtistScreenItem.SectionHeaderItem(AlbumType.ALBUM))
                        }
                        addAll(artistWithAlbums.artistAlbums.map { it.toAlbumRowItem() })
                        if (artistWithAlbums.artistAppearsOn.isNotEmpty()) {
                            add(ArtistScreenItem.SectionHeaderItem(AlbumType.APPEARS_ON))
                        }
                        addAll(artistWithAlbums.artistAppearsOn.map { it.toAlbumRowItem() })
                    }
                )
            }
        }
    }

    override fun handle(action: ArtistUserAction) {
        when (action) {
            is ArtistUserAction.AlbumClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackList(
                            TrackListArguments(trackList = MediaGroup.Album(albumId = action.albumId))
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
    val listItems: List<ArtistScreenItem>,
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
            listItems = listOf(),
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