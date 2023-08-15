package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.util.AlbumType
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
    arguments: ArtistArguments,
    artistRepository: ArtistRepository,
) : BaseViewModel<ArtistState, ArtistUserAction>() {
    private val artistId = arguments.artistId

    init {
        viewModelScope.launch {
            val artistWithAlbums = artistRepository.getArtist(artistId).first()
            setDataState {
                it.copy(
                    artistName = artistWithAlbums.artist.artistName,
                    listItems = buildList {
                        if (artistWithAlbums.artistAlbums.isNotEmpty()) {
                            add(ArtistScreenItem.SectionHeaderItem(AlbumType.ALBUM))
                        }
                        addAll(artistWithAlbums.artistAlbums.map { album -> album.toAlbumRowItem() })
                        if (artistWithAlbums.artistAppearsOn.isNotEmpty()) {
                            add(ArtistScreenItem.SectionHeaderItem(AlbumType.APPEARS_ON))
                        }
                        addAll(artistWithAlbums.artistAppearsOn.map { album -> album.toAlbumRowItem() })
                    }
                )
            }
        }
    }

    override fun handle(action: ArtistUserAction) = Unit

    override val defaultState: ArtistState by lazy {
        ArtistState(
            artistName = "",
            listItems = listOf(),
        )
    }

}

private fun Album.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
    return ArtistScreenItem.AlbumRowItem(this.toModelListItemState())
}

data class ArtistState(
    val artistName: String,
    val listItems: List<ArtistScreenItem>,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object ArtistArgumentsModule {
    @Provides
    @ViewModelScoped
    fun provideArtistArguments(savedStateHandle: SavedStateHandle): ArtistArguments {
        return savedStateHandle.navArgs()
    }
}

data class ArtistArguments(val artistId: Long)


sealed interface ArtistUserAction : UserAction