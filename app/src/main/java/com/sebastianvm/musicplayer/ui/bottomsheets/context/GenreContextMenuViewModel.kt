package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
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
                playbackManager.playGenre(state.value.genreName).onEach {
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
                addUiEvent(BaseContextMenuUiEvent.NavigateToGenre(genreName = state.value.genreName))
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
    override val menuTitle: String,
    override val playbackResult: PlaybackResult? = null,
    val genreName: String,
    val mediaGroup: MediaGroup,
) : BaseContextMenuState(listItems, menuTitle)


@InstallIn(ViewModelComponent::class)
@Module
object InitialGenreContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialGenreContextMenuStateProvider(savedStateHandle: SavedStateHandle): GenreContextMenuState {
        val genreName = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val mediaGroupType =
            MediaGroupType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        val mediaGroupMediaId = savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_ID)!!
        return GenreContextMenuState(
            genreName = genreName,
            menuTitle = genreName,
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewGenre
            ),
        )
    }
}


