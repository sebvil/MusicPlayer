package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
        artistRepository.getArtist(artistId = state.mediaId).onEach {
            setState {
                copy(menuTitle = it.artist.artistName)
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playArtist(state.mediaId).onEach {
                    when (it) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                            copy(
                                playbackResult = it
                            )
                        }

                        is PlaybackResult.Success -> {}
                    }
                }.launchIn(viewModelScope)

            }

            is ContextMenuItem.ViewArtist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        ArtistRouteDestination(
                            ArtistArguments(
                                artistId = state.mediaId
                            )
                        )
                    )
                )
            }

            else -> throw IllegalStateException("Invalid row for artist context menu")
        }
    }

    override fun onPlaybackErrorDismissed() {
        setState { copy(playbackResult = null) }
    }
}

data class ArtistContextMenuArguments(val artistId: Long)


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
        val args = savedStateHandle.navArgs<ArtistContextMenuArguments>()
        return ArtistContextMenuState(
            mediaId = args.artistId,
            menuTitle = "",
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewArtist
            ),
        )
    }
}


