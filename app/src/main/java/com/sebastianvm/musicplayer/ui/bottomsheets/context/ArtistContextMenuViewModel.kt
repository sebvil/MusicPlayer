package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ArtistContextMenuViewModel(
    arguments: ArtistContextMenuArguments,
    artistRepository: ArtistRepository,
) : BaseContextMenuViewModel() {

    private val artistId = arguments.artistId

    init {
        artistRepository.getArtist(artistId = artistId).onEach { artistWithAlbums ->
            setDataState {
                it.copy(menuTitle = artistWithAlbums.artist.artistName)
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.ViewArtist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        ArtistRouteDestination(
                            ArtistArguments(
                                artistId = artistId
                            )
                        )
                    )
                )
            }

            else -> error("Invalid row for artist context menu")
        }
    }

    override fun onPlaybackErrorDismissed() {
        setDataState { it.copy(playbackResult = null) }
    }

    override val defaultState: ContextMenuState by lazy {
        ContextMenuState(
            menuTitle = "",
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewArtist
            )
        )
    }
}

data class ArtistContextMenuArguments(val artistId: Long)
