package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
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
class ContextMenuViewModel @Inject constructor(initialState: ContextMenuState) :
    BaseViewModel<ContextMenuUserAction, ContextMenuUiEvent, ContextMenuState>(initialState) {

    override fun handle(action: ContextMenuUserAction) {
        TODO("Not yet implemented")
    }
}

data class ContextMenuState(
    val listItems: List<ContextMenuItem>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialContextMenuStateProvider(savedStateHandle: SavedStateHandle): ContextMenuState {
        val screen = savedStateHandle.get<String>("screen")!!
        return ContextMenuState(
            listItems = contextMenuItemsForScreen(screen)
        )
    }
}

fun contextMenuItemsForScreen(screen: String): List<ContextMenuItem> {
    return when (screen) {
        NavRoutes.TRACKS_ROOT -> {
            listOf(
                ContextMenuItem.Play,
                ContextMenuItem.ViewArtists,
                ContextMenuItem.ViewAlbums
            )
        }
        else -> {
            listOf()
        }
    }
}


sealed class ContextMenuUserAction : UserAction
sealed class ContextMenuUiEvent : UiEvent

