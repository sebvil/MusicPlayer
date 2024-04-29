package com.sebastianvm.musicplayer.features.artist.screen

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolder
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.AlbumType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

class ArtistStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistArguments,
    artistRepository: ArtistRepository,
    private val albumContextMenuStateHolderFactory: AlbumContextMenuStateHolderFactory
) : StateHolder<UiState<ArtistState>, ArtistUserAction> {

    private val artistId = arguments.artistId
    private val contextMenuAlbumId: MutableStateFlow<Long?> = MutableStateFlow(null)

    override val state: StateFlow<UiState<ArtistState>> =
        combine(
            artistRepository.getArtist(artistId),
            contextMenuAlbumId
        ) { artistWithAlbums, contextMenuAlbumId ->
            val listItems = buildList {
                if (artistWithAlbums.artistAlbums.isNotEmpty()) {
                    add(ArtistScreenItem.SectionHeaderItem(AlbumType.ALBUM))
                }
                addAll(artistWithAlbums.artistAlbums.map { album -> album.toAlbumRowItem() })
                if (artistWithAlbums.artistAppearsOn.isNotEmpty()) {
                    add(ArtistScreenItem.SectionHeaderItem(AlbumType.APPEARS_ON))
                }
                addAll(artistWithAlbums.artistAppearsOn.map { album -> album.toAlbumRowItem() })
            }
            if (listItems.isEmpty()) {
                Empty
            } else {
                Data(
                    ArtistState(
                        artistName = artistWithAlbums.artist.artistName,
                        listItems = listItems,
                        albumContextMenuStateHolder = contextMenuAlbumId?.let { albumId ->
                            albumContextMenuStateHolderFactory.getStateHolder(
                                AlbumContextMenuArguments(albumId = albumId)
                            )
                        }
                    )
                )
            }
        }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: ArtistUserAction) {
        when (action) {
            is ArtistUserAction.AlbumContextMenuDismissed -> {
                contextMenuAlbumId.update { null }
            }

            is ArtistUserAction.AlbumMoreIconClicked -> {
                contextMenuAlbumId.update { action.albumId }
            }
        }
    }
}

private fun Album.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
    return ArtistScreenItem.AlbumRowItem(this.toModelListItemState())
}

data class ArtistState(
    val artistName: String,
    val listItems: List<ArtistScreenItem>,
    val albumContextMenuStateHolder: AlbumContextMenuStateHolder?
) : State

@Serializable
data class ArtistArguments(val artistId: Long)

sealed interface ArtistUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : ArtistUserAction
    data object AlbumContextMenuDismissed : ArtistUserAction
}
