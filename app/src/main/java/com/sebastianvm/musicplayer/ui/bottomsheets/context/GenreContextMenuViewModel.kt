package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
class GenreContextMenuViewModel @Inject constructor(
    initialState: GenreContextMenuState,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<GenreContextMenuState>(initialState) {

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playGenre(state.value.mediaId).onEach {
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
            is ContextMenuItem.ViewGenre -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToGenre(genreId = state.value.mediaId))
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
        val genreId = 0L

        return GenreContextMenuState(
            mediaId = genreId,
            menuTitle = "",
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewGenre
            ),
        )
    }
}


