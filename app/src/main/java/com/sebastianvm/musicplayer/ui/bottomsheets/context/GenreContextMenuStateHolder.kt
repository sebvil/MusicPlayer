package com.sebastianvm.musicplayer.ui.bottomsheets.context

import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GenreContextMenuStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: GenreContextMenuArguments,
    genreRepository: GenreRepository,
) : BaseContextMenuStateHolder() {

    private val genreId = arguments.genreId

    override val state: StateFlow<UiState<ContextMenuState>> =
        genreRepository.getGenreName(genreId = arguments.genreId).map { genre ->
            Data(
                ContextMenuState(
                    menuTitle = genre,
                    listItems = listOf(
                        ContextMenuItem.PlayAllSongs,
                        ContextMenuItem.ViewGenre(genreId = genreId)
                    )
                )
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun onRowClicked(row: ContextMenuItem) {
        error("Invalid row for genre context menu")
    }
}

data class GenreContextMenuArguments(val genreId: Long)
