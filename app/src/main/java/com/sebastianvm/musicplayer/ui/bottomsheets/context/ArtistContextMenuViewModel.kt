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
    arguments: ArtistContextMenuArguments,
    artistRepository: ArtistRepository,
    private val playbackManager: PlaybackManager,
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
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playArtist(artistId).onEach { result ->
                    when (result) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> {
                            setDataState {
                                it.copy(
                                    playbackResult = result
                                )
                            }
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
                                artistId = artistId
                            )
                        )
                    )
                )
            }

            else -> throw IllegalStateException("Invalid row for artist context menu")
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

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistContextMenuArgumentsProvider(savedStateHandle: SavedStateHandle): ArtistContextMenuArguments {
        return savedStateHandle.navArgs()
    }
}


