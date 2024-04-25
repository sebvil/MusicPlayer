package com.sebastianvm.musicplayer.ui.artist

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.util.AlbumType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ArtistViewModel(
    initialState: ArtistState = ArtistState(
        artistName = "",
        listItems = listOf(),
        isLoading = true
    ),
    viewModelScope: CoroutineScope? = null,
    arguments: ArtistArguments,
    artistRepository: ArtistRepository
) : BaseViewModel<ArtistState, ArtistUserAction>(
    initialState = initialState,
    viewModelScope = viewModelScope
) {

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

data class ArtistArguments(val artistId: Long)

sealed interface ArtistUserAction : UserAction

fun ArtistState.toUiState(): UiState<ArtistState> {
    return when {
        isLoading -> Loading
        listItems.isEmpty() -> Empty
        else -> Data(this)
    }
}
