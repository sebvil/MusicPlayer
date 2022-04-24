package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumContextMenuViewModel @Inject constructor(
    initialState: AlbumContextMenuState,
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<AlbumContextMenuState>(initialState) {

    private var trackIds: List<String> = listOf()
    private var artistIds: List<String> = listOf()

    init {
        collect(albumRepository.getAlbum(state.value.albumId)) {
            trackIds = it.tracks
            artistIds = it.artists
            setState {
                copy(
                    menuTitle = it.album.albumName,
                    listItems = listOf(
                        ContextMenuItem.PlayFromBeginning,
                        ContextMenuItem.AddToQueue,
                        if (artistIds.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                        ContextMenuItem.ViewAlbum
                    )
                )
            }
        }
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayFromBeginning -> {
                viewModelScope.launch {
                    playbackManager.playAlbum(state.value.albumId)
                    addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                }
            }
            is ContextMenuItem.AddToQueue -> {
                viewModelScope.launch {
                    playbackManager.addToQueue(trackIds)
                }
            }
            is ContextMenuItem.ViewAlbum -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToAlbum(state.value.albumId))
            }
            is ContextMenuItem.ViewArtists -> {
                addUiEvent(
                    BaseContextMenuUiEvent.NavigateToArtistsBottomSheet(
                        state.value.albumId,
                        MediaType.ALBUM
                    )
                )
            }
            is ContextMenuItem.ViewArtist -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToArtist(artistIds[0]))
            }
            else -> throw IllegalStateException("Invalid row for album context menu")
        }
    }
}

data class AlbumContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val albumId: String,
) : BaseContextMenuState(listItems, menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumContextMenuStateProvider(savedStateHandle: SavedStateHandle): AlbumContextMenuState {
        val albumId = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        return AlbumContextMenuState(
            albumId = albumId,
            menuTitle = "",
            listItems = listOf(),
        )
    }
}


