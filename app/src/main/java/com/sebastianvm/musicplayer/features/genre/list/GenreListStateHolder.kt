package com.sebastianvm.musicplayer.features.genre.list

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuStateHolder
import com.sebastianvm.musicplayer.features.genre.menu.genreContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface GenreListDelegate {
    fun showGenre(arguments: TrackListArguments)
}

class GenreListStateHolder(
    genreRepository: GenreRepository,
    private val delegate: GenreListDelegate,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val genreContextMenuStateHolderFactory: StateHolderFactory<GenreContextMenuArguments, GenreContextMenuStateHolder>,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<UiState<GenreListState>, GenreListUserAction> {

    private val contextMenuGenreId = MutableStateFlow<Long?>(null)

    override val state: StateFlow<UiState<GenreListState>> = combine(
        genreRepository.getGenres(),
        sortPreferencesRepository.getGenreListSortOrder(),
        contextMenuGenreId,
    ) { genres, sortOrder, contextMenuGenreId ->
        if (genres.isEmpty()) {
            Empty
        } else {
            Data(
                GenreListState(
                    modelListState = ModelListState(
                        items = genres.map { genre ->
                            genre.toModelListItemState()
                        },
                        sortButtonState = SortButtonState(
                            text = R.string.genre_name,
                            sortOrder = sortOrder
                        )
                    ),
                    genreContextMenuStateHolder = contextMenuGenreId?.let { genreId ->
                        genreContextMenuStateHolderFactory.getStateHolder(
                            GenreContextMenuArguments(
                                genreId
                            )
                        )
                    }
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: GenreListUserAction) {
        when (action) {
            is GenreListUserAction.SortByButtonClicked -> {
                stateHolderScope.launch {
                    sortPreferencesRepository.toggleGenreListSortOrder()
                }
            }

            is GenreListUserAction.GenreMoreIconClicked -> {
                contextMenuGenreId.update { action.genreId }
            }

            is GenreListUserAction.GenreContextMenuDismissed -> {
                contextMenuGenreId.update { null }
            }

            is GenreListUserAction.GenreClicked -> {
                delegate.showGenre(TrackListArguments(trackListType = MediaGroup.Genre(action.genreId)))
            }
        }
    }
}

data class GenreListState(
    val modelListState: ModelListState,
    val genreContextMenuStateHolder: GenreContextMenuStateHolder?
) : State

sealed interface GenreListUserAction : UserAction {
    data object SortByButtonClicked : GenreListUserAction
    data class GenreClicked(val genreId: Long) : GenreListUserAction
    data class GenreMoreIconClicked(val genreId: Long) : GenreListUserAction
    data object GenreContextMenuDismissed : GenreListUserAction
}

@Composable
fun rememberGenreListStateHolder(): GenreListStateHolder {
    val genreContextMenuStateHolderFactory = genreContextMenuStateHolderFactory()
    return stateHolder { dependencies ->
        GenreListStateHolder(
            genreRepository = dependencies.repositoryProvider.genreRepository,
            delegate = object : GenreListDelegate {
                override fun showGenre(arguments: TrackListArguments) {
                    TODO("navigation")
                }
            },
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
            genreContextMenuStateHolderFactory = genreContextMenuStateHolderFactory
        )
    }
}