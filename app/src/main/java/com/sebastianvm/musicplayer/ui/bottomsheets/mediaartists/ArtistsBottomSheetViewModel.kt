package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.library.artists.ArtistsBottomSheetListItem
import com.sebastianvm.musicplayer.ui.library.artists.ArtistsListItem
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
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
class ArtistsBottomSheetViewModel @Inject constructor(initialState: ArtistsBottomSheetState) :
    BaseViewModel<ArtistsBottomSheetUserAction, ArtistsBottomSheetUiEvent, ArtistsBottomSheetState>(
        initialState
    ) {

    override fun handle(action: ArtistsBottomSheetUserAction) {
        TODO("Not yet implemented")
    }
}

data class ArtistsBottomSheetState(
    val mediaGroup: MediaGroup,
    val artistsList: List<ArtistsListItem>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistsBottomSheetStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistsBottomSheetStateProvider(savedStateHandle: SavedStateHandle): ArtistsBottomSheetState {
        val mediaType = MediaType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_TYPE)!!)
        val mediaGroupType = MediaType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        return ArtistsBottomSheetState()
    }
}

sealed class ArtistsBottomSheetUserAction : UserAction
sealed class ArtistsBottomSheetUiEvent : UiEvent

