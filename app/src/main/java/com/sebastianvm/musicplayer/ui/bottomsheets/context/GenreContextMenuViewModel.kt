package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
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
    arguments: GenreContextMenuArguments,
    genreRepository: GenreRepository,
    private val playbackManager: PlaybackManager
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
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playGenre(genreId).onEach { result ->
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

            else -> throw IllegalStateException("Invalid row for genre context menu")
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

@InstallIn(ViewModelComponent::class)
@Module
object InitialGenreContextMenuArgumentsModule {
    @Provides
    @ViewModelScoped
    fun genreContextMenuArgumentsProvider(savedStateHandle: SavedStateHandle): GenreContextMenuArguments {
        return savedStateHandle.navArgs()
    }
}
