package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.util.AlbumType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel(
    initialState: ArtistState,
    viewModelScope: CoroutineScope?,
    arguments: ArtistArguments,
    artistRepository: ArtistRepository
) : BaseViewModel<ArtistState, ArtistUserAction>(
    initialState = initialState,
    viewModelScope = viewModelScope
) {

    @Inject
    constructor(arguments: ArtistArguments, artistRepository: ArtistRepository) : this(
        initialState = ArtistState(
            artistName = "",
            listItems = listOf(),
            isLoading = true
        ),
        viewModelScope = null,
        arguments = arguments,
        artistRepository = artistRepository
    )

    private val artistId = arguments.artistId

    init {
        artistRepository.getArtist(artistId).onEach { artistWithAlbums ->
            setState {
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
                    },
                    isLoading = false
                )
            }
        }.launchIn(vmScope)
    }

    override fun handle(action: ArtistUserAction) = Unit
}

private fun Album.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
    return ArtistScreenItem.AlbumRowItem(this.toModelListItemState())
}

data class ArtistState(
    val artistName: String,
    val listItems: List<ArtistScreenItem>,
    val isLoading: Boolean
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

fun ArtistState.toUiState(): UiState<ArtistState> {
    return when {
        isLoading -> Loading
        listItems.isEmpty() -> Empty
        else -> Data(this)
    }
}
