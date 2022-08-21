package com.sebastianvm.musicplayer.ui.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    initialState: AlbumState,
    albumRepository: AlbumRepository,
) : BaseViewModel<AlbumUiEvent, AlbumState>(initialState),
    ViewModelInterface<AlbumState, AlbumUserAction> {

    init {
        viewModelScope.launch {
            val albumWithTracks = albumRepository.getAlbum(state.value.albumId).first()
            setState {
                copy(
                    imageUri = albumWithTracks.imageUri,
                    albumName = albumWithTracks.albumName,
                )
            }
        }
    }

    override fun handle(action: AlbumUserAction) = Unit

}

data class AlbumState(
    val albumId: Long,
    val imageUri: String,
    val albumName: String,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumStateModule {
    @Provides
    @ViewModelScoped
    fun provideInitialAlbumState(savedStateHandle: SavedStateHandle): AlbumState {
        val args = savedStateHandle.getArgs<AlbumArguments>()
        return AlbumState(
            albumId = args.albumId,
            imageUri = "",
            albumName = "",
        )
    }
}

sealed class AlbumUiEvent : UiEvent
sealed interface AlbumUserAction : UserAction