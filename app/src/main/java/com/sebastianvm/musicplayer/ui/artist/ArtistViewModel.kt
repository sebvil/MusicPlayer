package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
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
) : BaseViewModel<ArtistState, ArtistUserAction, UiEvent>(initialState) {
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

    override fun handle(action: ArtistUserAction) = Unit

}

private fun Album.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
    return ArtistScreenItem.AlbumRowItem(this.toModelListItemState())
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

sealed interface ArtistUserAction : UserAction