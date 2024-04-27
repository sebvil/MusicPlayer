package com.sebastianvm.musicplayer.ui.bottomsheets.context

import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ArtistContextMenuStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistContextMenuArguments,
    artistRepository: ArtistRepository,
) : BaseContextMenuStateHolder() {

    private val artistId = arguments.artistId

    override val state: StateFlow<UiState<ContextMenuState>> =
        artistRepository.getArtist(artistId = artistId).map { artistWithAlbums ->
            Data(
                ContextMenuState(
                    menuTitle = artistWithAlbums.artist.artistName,
                    listItems = listOf(
                        ContextMenuItem.PlayAllSongs,
                        ContextMenuItem.ViewArtist(artistId)
                    )
                )
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun onRowClicked(row: ContextMenuItem) {
        error("Invalid row for artist context menu $row")
    }
}

data class ArtistContextMenuArguments(val artistId: Long)
