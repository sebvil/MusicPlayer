package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GenreContextMenuViewModel(
    arguments: GenreContextMenuArguments,
    genreRepository: GenreRepository,
) : BaseContextMenuViewModel() {

    private val genreId = arguments.genreId

    init {
        genreRepository.getGenreName(genreId = genreId).onEach { genre ->
            setDataState {
                it.copy(menuTitle = genre)
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.ViewGenre -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        TrackListRouteDestination(
                            TrackListArgumentsForNav(
                                trackListType = MediaGroup.Genre(genreId)
                            )
                        )
                    )
                )
            }

            else -> error("Invalid row for genre context menu")
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
                ContextMenuItem.ViewGenre
            )
        )
    }
}

data class GenreContextMenuArguments(val genreId: Long)
