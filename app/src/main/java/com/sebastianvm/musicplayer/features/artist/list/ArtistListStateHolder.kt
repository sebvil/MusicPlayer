package com.sebastianvm.musicplayer.features.artist.list

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuStateHolder
import com.sebastianvm.musicplayer.features.artist.menu.artistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
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

interface ArtistListDelegate {
    fun showArtist(arguments: ArtistArguments)
}

data class ArtistListState(
    val modelListState: ModelListState,
    val artistContextMenuStateHolder: ArtistContextMenuStateHolder?
) : State

sealed interface ArtistListUserAction : UserAction {
    data object SortByButtonClicked : ArtistListUserAction
    data class ArtistMoreIconClicked(val artistId: Long) : ArtistListUserAction
    data object ArtistContextMenuDismissed : ArtistListUserAction
    data class ArtistClicked(val artistId: Long) : ArtistListUserAction
}

class ArtistListStateHolder(
    artistRepository: ArtistRepository,
    private val delegate: ArtistListDelegate,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val artistContextMenuStateHolderFactory: StateHolderFactory<ArtistContextMenuArguments, ArtistContextMenuStateHolder>,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<UiState<ArtistListState>, ArtistListUserAction> {

    private val contextMenuArtistId = MutableStateFlow<Long?>(null)
    override val state: StateFlow<UiState<ArtistListState>> = combine(
        artistRepository.getArtists(),
        sortPreferencesRepository.getArtistListSortOrder(),
        contextMenuArtistId,
    ) { artists, sortOrder, contextMenuArtistId ->
        if (artists.isEmpty()) {
            Empty
        } else {
            Data(
                ArtistListState(
                    modelListState = ModelListState(
                        items = artists.map { artist ->
                            artist.toModelListItemState(trailingButtonType = TrailingButtonType.More)
                        },
                        sortButtonState = SortButtonState(
                            text = R.string.artist_name,
                            sortOrder = sortOrder
                        ),
                        headerState = HeaderState.None
                    ),
                    artistContextMenuStateHolder = contextMenuArtistId?.let { artistId ->
                        artistContextMenuStateHolderFactory.getStateHolder(
                            ArtistContextMenuArguments(artistId)
                        )
                    }
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: ArtistListUserAction) {
        when (action) {
            is ArtistListUserAction.SortByButtonClicked -> {
                stateHolderScope.launch {
                    sortPreferencesRepository.toggleArtistListSortOrder()
                }
            }

            is ArtistListUserAction.ArtistMoreIconClicked -> {
                contextMenuArtistId.update { action.artistId }
            }

            is ArtistListUserAction.ArtistContextMenuDismissed -> {
                contextMenuArtistId.update { null }
            }

            is ArtistListUserAction.ArtistClicked -> {
                delegate.showArtist(ArtistArguments(action.artistId))
            }
        }
    }
}


@Composable
fun rememberArtistListStateHolder(): ArtistListStateHolder {
    val artistContextMenuStateHolderFactory = artistContextMenuStateHolderFactory()
    return stateHolder { dependencies ->
        ArtistListStateHolder(
            artistRepository = dependencies.repositoryProvider.artistRepository,
            delegate = object : ArtistListDelegate {
                override fun showArtist(arguments: ArtistArguments) {
                    TODO("navigation")
                }
            },
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
            artistContextMenuStateHolderFactory = artistContextMenuStateHolderFactory
        )
    }
}

