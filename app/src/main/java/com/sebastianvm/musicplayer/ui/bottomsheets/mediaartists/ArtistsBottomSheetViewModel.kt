package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class ArtistsViewModel @Inject constructor(initialState: ArtistsState) :
    BaseViewModel<ArtistsUserAction, ArtistsUiEvent, ArtistsState>(initialState) {

    override fun handle(action: ArtistsUserAction) {
        TODO("Not yet implemented")
    }
}

data class ArtistsState() : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistsStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistsStateProvider(savedStateHandle: SavedStateHandle): ArtistsState {
        return ArtistsState()
    }
}

sealed class ArtistsUserAction : UserAction
sealed class ArtistsUiEvent : UiEvent

