package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ArtistContextMenuViewModel @Inject constructor(
    initialState: ArtistContextMenuState,
    artistRepository: ArtistRepository,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<ArtistContextMenuState>(initialState) {

    init {
        artistRepository.getArtist(artistId = state.value.mediaId).onEach {
            setState {
                copy(menuTitle = it.artist.artistName)
            }
        }
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playArtist(state.value.mediaId).onEach {
                    when (it) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                            copy(
                                playbackResult = it
                            )
                        }
                        is PlaybackResult.Success -> addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                    }
                }.launchIn(viewModelScope)

            }
            is ContextMenuItem.ViewArtist -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToArtist(state.value.mediaId))
            }
            else -> throw IllegalStateException("Invalid row for artist context menu")
        }
    }

    override fun onPlaybackErrorDismissed() {
        setState { copy(playbackResult = null) }
    }
}

data class ArtistContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val mediaId: Long,
    override val menuTitle: String,
    override val playbackResult: PlaybackResult? = null,
) : BaseContextMenuState(listItems, mediaId, menuTitle, playbackResult)


@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistContextMenuStateProvider(savedStateHandle: SavedStateHandle): ArtistContextMenuState {
        val artistId = 0L
        return ArtistContextMenuState(
            mediaId = artistId,
            menuTitle = "",
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewArtist
            ),
        )
    }
}


