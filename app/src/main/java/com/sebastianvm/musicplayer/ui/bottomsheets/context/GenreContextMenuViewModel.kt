package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
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
class GenreContextMenuViewModel @Inject constructor(
    initialState: GenreContextMenuState,
    genreRepository: GenreRepository,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<GenreContextMenuState>(initialState) {

    init {
        genreRepository.getGenreName(genreId = state.mediaId).onEach {
            setState {
                copy(menuTitle = it)
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playGenre(state.mediaId).onEach {
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

            is ContextMenuItem.ViewGenre -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        TrackListRouteDestination(
                            TrackListArguments(
                                trackList = MediaGroup.Genre(state.mediaId)
                            )
                        )
                    )
                )
            }

            else -> throw IllegalStateException("Invalid row for genre context menu")
        }
    }

    override fun onPlaybackErrorDismissed() {
        setState { copy(playbackResult = null) }
    }
}

data class GenreContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val mediaId: Long,
    override val menuTitle: String,
    override val playbackResult: PlaybackResult? = null,
) : BaseContextMenuState(listItems, mediaId, menuTitle, playbackResult)


@InstallIn(ViewModelComponent::class)
@Module
object InitialGenreContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialGenreContextMenuStateProvider(savedStateHandle: SavedStateHandle): GenreContextMenuState {
        val args = savedStateHandle.navArgs<GenreContextMenuArguments>()
        return GenreContextMenuState(
            mediaId = args.genreId,
            menuTitle = "",
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewGenre
            ),
        )
    }
}


